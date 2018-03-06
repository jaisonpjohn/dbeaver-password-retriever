
import java.util.ArrayList;
import java.util.List;


public class CombinationFinder {
  public static void main(String args[]) {
    // [1,2,5,7,10]
    // 1 + 2 + 7 = 10 ==> return true
    int[] input = {1,2,5,7,10};
    //int[] input = {1,2,5,7,1,8,9,10};
    int targetSum = input[input.length-1];
    System.out.println(findCombination(input,0,targetSum,new ArrayList<>()));
  }

  private static boolean findCombination(int[] numbers,int begin, int targetSum, List<Integer> combinations) {
    int sum = 0;
    for (int number: combinations){
      sum += number;
    }
    if (sum == targetSum){
      System.out.println(combinations);
      return true;
    } else if (sum > targetSum){
      return false;
    }
    // Else
    for(int i=begin; i<numbers.length - 1; i++) {
      List<Integer> newCombinations = new ArrayList<>(combinations);
      newCombinations.add(numbers[i]);
      if(findCombination(numbers,++begin,targetSum,newCombinations)){
        // To return with the first occurrence of a valid combination
        return true;
      }
    }
    return false;
  }


}
