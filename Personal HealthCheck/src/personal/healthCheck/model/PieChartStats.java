package personal.healthCheck.model;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.R;
import android.content.Context;
import android.graphics.Color;

public class PieChartStats {

		public GraphicalView getPieChartForMonth(Context context) {
			 
			int[] colors = new int[] { Color.RED, Color.YELLOW, Color.BLUE };
			DefaultRenderer renderer = buildCategoryRenderer(colors);
			 
			CategorySeries categorySeries = new CategorySeries("Events");
			categorySeries.add("late", 30);
			categorySeries.add("missed", 20);
			categorySeries.add("on time ", 60);
			return ChartFactory.getPieChartView(context, categorySeries, renderer);
			}
		 
		protected DefaultRenderer buildCategoryRenderer(int[] colors) {
		DefaultRenderer renderer = new DefaultRenderer();
		for (int color : colors) {
		SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		r.setColor(color);
		renderer.addSeriesRenderer(r);
		}
		renderer.setChartTitle("Monthly Statistics - NOT COMPLETED");
		renderer.setChartTitleTextSize(20);				
		renderer.setPanEnabled(false);
		renderer.setZoomButtonsVisible(true);
		renderer.setLabelsTextSize(20);
		renderer.setBackgroundColor(R.color.black);
		return renderer;
		}
}
