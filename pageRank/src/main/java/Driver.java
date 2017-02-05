/**
 * Created by zhangshiqiu on 2017/2/5.
 */
public class Driver {
    public static void main(String[] args) throws Exception{
        UnitMultiplication multiplication = new UnitMultiplication();
        UnitSum sum = new UnitSum();

        String transitionMatrix = args[0]; // path of transition.txt
        String prMatrix = args[1]; // path of pr
        String subPageRank = args[2]; // path of subPR

        int numOfIteration = Integer.parseInt(args[3]);  // number of iteration

        for (int i = 0; i < numOfIteration; i++) {
            String[] args1 = {transitionMatrix, prMatrix + i, subPageRank + i};
            multiplication.main(args1);
            String[] args2 = {subPageRank + i, prMatrix + (i+1)};
            sum.main(args2);
        }
    }
}
