package ro.hasna.ts.math.ml.distance;

import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.util.FastMath;
import ro.hasna.ts.math.representation.PiecewiseLinearAggregateApproximation;
import ro.hasna.ts.math.type.MeanSlopePair;

/**
 * Calculates the L<sub>2</sub> (Euclidean) distance between two vectors using the PLAA representation.
 *
 * @since 1.0
 */
public class PlaaEuclideanDistance extends AbstractTimeSeriesDistance implements DistanceMeasure, GenericDistanceMeasure<MeanSlopePair[]> {
    private final PiecewiseLinearAggregateApproximation plaa;

    public PlaaEuclideanDistance(PiecewiseLinearAggregateApproximation plaa) {
        this.plaa = plaa;
    }

    @Override
    public double compute(double[] a, double[] b) {
        MeanSlopePair[] mspA = plaa.transform(a);
        MeanSlopePair[] mspB = plaa.transform(b);
        int n = a.length;

        return compute(mspA, mspB, n, Double.POSITIVE_INFINITY);
    }

    @Override
    public double compute(MeanSlopePair[] a, MeanSlopePair[] b) {
        return compute(a, b, initialVectorLength, Double.POSITIVE_INFINITY);
    }

    @Override
    public double compute(MeanSlopePair[] a, MeanSlopePair[] b, double cutoff) {
        return compute(a, b, initialVectorLength, Double.POSITIVE_INFINITY);
    }

    /**
     * Compute the distance between two PLAA representations.
     *
     * @param a      the first PLAA representation
     * @param b      the second representation
     * @param n      the length of the initial vectors
     * @param cutoff if the distance being calculated is above this value stop computing the remaining distance
     * @return the distance between the two representations
     */
    public double compute(MeanSlopePair[] a, MeanSlopePair[] b, int n, double cutoff) {
        double sum1 = 0.0;
        double sum2 = 0.0;
        int w = a.length;
        double k = n * 1.0 / w;
        double transformedCutoff = cutoff * cutoff;

        for (int i = 0; i < w; i++) {
            double aux = a[i].getMean() - b[i].getMean();
            sum1 += aux * aux;

            aux = a[i].getSlope() - b[i].getSlope();
            sum2 += aux * aux;

            double currentDistance = sum1 * k + sum2 * (k * k - 1) / 18;
            if (currentDistance > transformedCutoff) {
                return FastMath.sqrt(currentDistance);
            }
        }

        sum1 = sum1 * k;
        sum2 = sum2 * (k * k - 1) / 18;

        return FastMath.sqrt(sum1 + sum2);
    }
}