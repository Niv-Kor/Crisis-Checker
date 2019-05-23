package albert.utility.math;
import java.awt.Dimension;

public class Percentage
{
	public final static Range<Double> COMMON_PERCENT_RANGE = new Range<Double>(0.0, 100.0); 
	
	/**
	 * Calculate the percentage of a numerator out of the denominator.
	 * (Ex. 80 (numerator) out of 122 (denominator) is 65.57%).
	 * @param numerator - The number to divide
	 * @param denominator - The divisor
	 * @return the percentage of the numerator divide by the denominator.
	 */
	public static <T extends Number> double numOfNum(T numerator, T denominator) {
		return numerator.doubleValue() / denominator.doubleValue() * 100;
	}
	
	/**
	 * Calculate the number that X% of Y is.
	 * (Ex. 80% (percent) of 122 (number) is 97.6).
	 * @param perc - The percentage
	 * @param number - The number to take percentage of
	 * @return a number that is X% of Y. 
	 */
	public static <T extends Number> double percentOfNum(T perc, T number) {
		return number.doubleValue() / 100 * perc.doubleValue();
	}
	
	/**
	 * Limit a number to fit inside a percentage range of 0-100.
	 * If a number is under 0 - return 0;
	 * If a number is over 100 - return 100;
	 * If a number is between 0-100 - do nothing.
	 * @param number - The number to limit
	 * @return a value that's between 0-100.
	 */
	public static double limit(double number) {
		if (number > COMMON_PERCENT_RANGE.getMax()) return COMMON_PERCENT_RANGE.getMax();
		else if (number < COMMON_PERCENT_RANGE.getMin()) return COMMON_PERCENT_RANGE.getMin();
		else return number;
	}
	
	/**
	 * Limit a number to fit inside the requested range.
	 * If a number is under the minimum - return minimum;
	 * If a number is over the maximum - return maximum;
	 * If a number is within range - do nothing.
	 * @param d - The number to limit
	 * @return a value that's within the range.
	 */
	public static <T extends Number> double limit(double num, Range<? extends Number> effectModel) {
		double effectMin = effectModel.getMin().doubleValue();
		double effectMax = effectModel.getMax().doubleValue();
		
		if (effectMin < effectMax) {
			if (num > effectModel.getMax().doubleValue()) return effectModel.getMax().doubleValue();
			else if (num < effectModel.getMin().doubleValue()) return effectModel.getMin().doubleValue();
		}
		else {
			if (num > effectModel.getMin().doubleValue()) return effectModel.getMax().doubleValue();
			else if (num < effectModel.getMax().doubleValue()) return effectModel.getMin().doubleValue();
		}
		
		return num;
	}
	
	public static Dimension createDimension(Dimension source, double widthPercent, double heightPercent) {
		int width = (int) percentOfNum(widthPercent, source.width);
		int height = (int) percentOfNum(heightPercent, source.height);
		return new Dimension(width, height);
	}
}