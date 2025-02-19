package ai.adminco.blog.l20250123;

import java.math.BigDecimal;

public class TemporalCoupling {
   private Integer count;
   private Integer totalTime;

   public void init() {
	  count = 1000; // Load count from database
	  totalTime = 233232332; // Load the total time from the database
   }

   public BigDecimal avgTime() {
	  final int SCALE = 15;
	  final BigDecimal dividend = BigDecimal.valueOf( totalTime );
	  final BigDecimal divisor = BigDecimal.valueOf( count );

	  int scale = SCALE > dividend.scale() ? SCALE : dividend.scale();
	  scale = scale > divisor.scale() ? scale : divisor.scale();
	  final BigDecimal result = dividend.divide( divisor, scale, BigDecimal.ROUND_HALF_EVEN );
	  return result;
   }
}
