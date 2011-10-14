/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tablekit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ITableAdapter;
import org.eclipse.swt.widgets.*;


public final class TableLCAUtil {

  // Constants used to preserve values
  public static final String PROP_ALIGNMENT = "alignment";
  static final String PROP_ITEM_METRICS = "itemMetrics";
  static final String PROP_FOCUS_INDEX = "focusIndex";

  // Constants used by alignment
  private static final Integer DEFAULT_ALIGNMENT = new Integer( SWT.LEFT );
  private static final Integer DEFAULT_FOCUS_INDEX = new Integer( -1 );

  ////////////////////////////
  // Column and Item alignment

  public static void preserveAlignment( final TableColumn column ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( column );
    adapter.preserve( PROP_ALIGNMENT, new Integer( column.getAlignment() ) );
  }

  public static boolean hasAlignmentChanged( final Table table ) {
    boolean result = false;
    TableColumn[] columns = table.getColumns();
    for( int i = 0; !result && i < columns.length; i++ ) {
      if( hasAlignmentChanged( columns[ i ] ) ) {
        result = true;
      }
    }
    return result;
  }

  public static boolean hasAlignmentChanged( final TableColumn column ) {
    return WidgetLCAUtil.hasChanged( column,
                                     PROP_ALIGNMENT,
                                     new Integer( column.getAlignment() ),
                                     DEFAULT_ALIGNMENT );
  }

  ///////////////
  // Item metrics

  public static void preserveItemMetrics( final Table table ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( table );
    adapter.preserve( PROP_ITEM_METRICS, getItemMetrics( table ) );
  }

  public static boolean hasItemMetricsChanged( final Table table ) {
    ItemMetrics[] itemMetrics = getItemMetrics( table );
    return hasItemMetricsChanged( table, itemMetrics );
  }

  public static void writeItemMetrics( final Table table ) throws IOException {
    ItemMetrics[] itemMetrics = getItemMetrics( table );
    if( hasItemMetricsChanged( table, itemMetrics ) ) {
      JSWriter writer = JSWriter.getWriterFor( table );
      for( int i = 0; i < itemMetrics.length; i++ ) {
        Object[] args = new Object[] {
          new Integer( i ),
          new Integer( itemMetrics[ i ].left ),
          new Integer( itemMetrics[ i ].width ),
          new Integer( itemMetrics[ i ].imageLeft ),
          new Integer( itemMetrics[ i ].imageWidth ),
          new Integer( itemMetrics[ i ].textLeft ),
          new Integer( itemMetrics[ i ].textWidth )
        };
        writer.set( "itemMetrics", args );
      }
    }
  }

  ////////
  // Focus

  public static void preserveFocusIndex( Table table ) {
    ITableAdapter tableAdapter = table.getAdapter( ITableAdapter.class );
    int focusIndex = tableAdapter.getFocusIndex();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( table );
    adapter.preserve( PROP_FOCUS_INDEX, new Integer( focusIndex ) );
  }

  public static boolean hasFocusIndexChanged( Table table ) {
    ITableAdapter tableAdapter = table.getAdapter( ITableAdapter.class );
    Integer focusIndex = new Integer( tableAdapter.getFocusIndex() );
    return WidgetLCAUtil.hasChanged( table,
                                     PROP_FOCUS_INDEX,
                                     focusIndex,
                                     DEFAULT_FOCUS_INDEX );
  }

  //////////////////
  // Helping methods

  private static boolean hasItemMetricsChanged( Table table, ItemMetrics[] metrics  ) {
    return WidgetLCAUtil.hasChanged( table, PROP_ITEM_METRICS, metrics );
  }

  static ItemMetrics[] getItemMetrics( Table table ) {
    int columnCount = Math.max( 1, table.getColumnCount() );
    ItemMetrics[] result = new ItemMetrics[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      result[ i ] = new ItemMetrics();
    }
    ITableAdapter tableAdapter = table.getAdapter( ITableAdapter.class );
    TableItem measureItem = tableAdapter.getMeasureItem();
    if( measureItem != null ) {
      for( int i = 0; i < columnCount; i++ ) {
        int leftOffset = tableAdapter.getColumnLeftOffset( i );
        Rectangle bounds = measureItem.getBounds( i );
        Rectangle imageBounds = measureItem.getImageBounds( i );
        Rectangle textBounds = measureItem.getTextBounds( i );
        // If in column mode, cut image width if image exceeds right cell border
        int imageWidth = tableAdapter.getItemImageWidth( i );
        if( table.getColumnCount() > 0 ) {
          TableColumn column = table.getColumn( i );
          int columnLeft = tableAdapter.getColumnLeft( column );
          int columnWidth = column.getWidth();
          int maxImageWidth = columnWidth - ( imageBounds.x - columnLeft + leftOffset );
          if( imageWidth > maxImageWidth ) {
            imageWidth = Math.max( 0, maxImageWidth );
          }
        }
        result[ i ].left = bounds.x + leftOffset;
        result[ i ].width = bounds.width;
        result[ i ].imageLeft = imageBounds.x + leftOffset;
        result[ i ].imageWidth = imageWidth;
        result[ i ].textLeft = textBounds.x + leftOffset;
        result[ i ].textWidth = textBounds.width;
      }
    } else if( table.getColumnCount() > 0 ) {
      for( int i = 0; i < columnCount; i++ ) {
        TableColumn column = table.getColumn( i );
        int columnLeft = tableAdapter.getColumnLeft( column );
        int columnWidth = column.getWidth();
        result[ i ].left = columnLeft;
        result[ i ].width = columnWidth;
      }
    }
    return result;
  }

  private TableLCAUtil() {
    // prevent instantiation
  }

  /////////////////
  // Inner classes

  static final class ItemMetrics {
    int left;
    int width;
    int imageLeft;
    int imageWidth;
    int textLeft;
    int textWidth;

    public boolean equals( final Object obj ) {
      boolean result;
      if( obj == this ) {
        result = true;
      } else  if( obj instanceof ItemMetrics ) {
        ItemMetrics other = ( ItemMetrics )obj;
        result =  other.left == left
               && other.width == width
               && other.imageLeft == imageLeft
               && other.imageWidth == imageWidth
               && other.textLeft == textLeft
               && other.textWidth == textWidth;
      } else {
        result = false;
      }
      return result;
    }

    public int hashCode() {
      String msg = "ItemMetrics#hashCode() not implemented";
      throw new UnsupportedOperationException( msg );
    }
  }
}
