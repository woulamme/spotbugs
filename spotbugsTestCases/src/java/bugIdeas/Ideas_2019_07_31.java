package bugIdeas;

import edu.umd.cs.findbugs.annotations.ExpectWarning;
import edu.umd.cs.findbugs.annotations.NoWarning;

/** I'd like spotbugs to protect me from comparisons that are unsafe due to unboxing. We spent some time recently trying
 * to debug a NullPointerException that was very hard to find. The problem was a bit like in potentiallyUnsafeButHardToFindCompareIntUsingUnboxing:
 * the type of two sides of a comparison were "hidden" because they came from an instance of an external class, so a
 * harmless int comparison was actually comparing an Integer that could be null against an int.
 * It would be great if spotbugs could warn against all unsafe comparisons that use unboxing.
 */
public class Ideas_2019_07_31 {
    @NoWarning("UNBOXING_WITH_POTENTIAL_NULL_POINTER")
    public boolean compareIntValueTypes(int a, int b) {
        return a == b;
    }

    @NoWarning("UNBOXING_WITH_POTENTIAL_NULL_POINTER")
    public boolean safeCompareIntUsingUnboxing(Integer a, int b) {
        return java.util.Objects.equals(a, b);
    }

    @NoWarning("UNBOXING_WITH_POTENTIAL_NULL_POINTER")
    public boolean compareBooleanValueTypes(boolean a, boolean b) {
        return a == b;
    }

    @NoWarning("UNBOXING_WITH_POTENTIAL_NULL_POINTER")
    public boolean safeCompareBooleanUsingUnboxing(Boolean a, boolean b) {
        return java.util.Objects.equals(a, b);
    }


    @ExpectWarning("UNBOXING_WITH_POTENTIAL_NULL_POINTER")
    public boolean potentiallyUnsafeCompareIntUsingUnboxing(Integer a, int b) {
        return a == b;
    }

    @ExpectWarning("UNBOXING_WITH_POTENTIAL_NULL_POINTER")
    public boolean potentiallyUnsafeCompareBooleanUsingUnboxing(Boolean a, boolean b) {
        return a == b;
    }

    @ExpectWarning("UNBOXING_WITH_POTENTIAL_NULL_POINTER")
    public boolean guaranteedUnsafeCompareIntUsingUnboxing(int b) {
        Integer a = null;
        return a == b;
    }

    @ExpectWarning("UNBOXING_WITH_POTENTIAL_NULL_POINTER")
    public boolean guaranteedUnsafeCompareBooleanUsingUnboxing(boolean b) {
        Boolean a = null;
        return a == b;
    }

    @ExpectWarning("UNBOXING_WITH_POTENTIAL_NULL_POINTER")
    public boolean potentiallyUnsafeButHardToFindCompareIntUsingUnboxing(int a, int b) {
        MemberTypesNotObviousFromCallingCode c = new MemberTypesNotObviousFromCallingCode(a, b);
        return c.getA() == c.getB();
    }

    private class MemberTypesNotObviousFromCallingCode {
        private final Integer a;
        private final int b;

        MemberTypesNotObviousFromCallingCode(Integer a, int b) {
            this.a = a;
            this.b = b;
        }

        public Integer getA() {
            return a;
        }

        public int getB() {
            return b;
        }
    }
}
