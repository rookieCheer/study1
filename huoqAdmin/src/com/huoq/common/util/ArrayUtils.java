package com.huoq.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

/**
 * 数组操作类
 * 
 * @author zhuhaojie 2016年10月9日下午5:50:47
 */
public class ArrayUtils {

    /**
     * 封闭无参数构造方法
     * 
     * @author zhuhaojie
     * @time 2017年1月3日 上午11:53:15
     */
    private ArrayUtils() {

    }

    /**
     * 交换数组中两元素
     * 
     * @since 1.1
     * @param ints
     *            需要进行交换操作的数组
     * @param x
     *            数组中的位置1
     * @param y
     *            数组中的位置2
     * @return 交换后的数组
     */
    public static int[] swap(int[] ints, int x, int y) {
        int temp = ints[x];
        ints[x] = ints[y];
        ints[y] = temp;
        return ints;
    }

    /**
     * 冒泡排序方法:相邻两元素进行比较 性能：比较次数O(n^2),n^2/2；交换次数O(n^2),n^2/4<br>
     * 冒泡排序（Bubble Sort）是一种简单的排序算法。它重复地走访过要排序的数列，一次比较两个元素，<br>
     * 如果他们的顺序错误就把他们交换过来。走访数列的工作是重复地进行直到没有再需要交换，<br>
     * 也就是说该数列已经排序完成。这个算法的名字由来是因为越小的元素会经由交换慢慢“浮”到数列的顶端。<br>
     * 冒泡排序算法的运作如下:<br>
     * 1. 比较相邻的元素。如果第一个比第二个大，就交换他们两个。<br>
     * 2. 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。在这一点，最后的元素应该会是最大的数。<br>
     * 3. 针对所有的元素重复以上的步骤，除了最后一个。<br>
     * 4. 持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较。<br>
     * 
     * @since 1.1
     * @param source
     *            需要进行排序操作的数组
     * @return 排序后的数组
     */
    public static int[] bubbleSort(int[] source) {

        for (int i = source.length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (source[j] > source[j + 1]) {
                    swap(source, j, j + 1);
                }
            }
        }
        return source;
    }

    /**
     * 选择排序法 方法：选择排序(Selection sort)是一种简单直观的排序算法，其平均时间复杂度为O(n2)。
     * 它的工作原理如下。首先在未排序序列中找到最小元素，存放到排序序列的起始位置，然后，
     * 再从剩余未排序元素中继续寻找最小元素，然后放到排序序列末尾。以此类推，直到所有元素均排序完毕。 性能：选择排序的交换操作介于0和(n-1)次之间，
     * 选择排序的比较操作为n(n-1)/2次之间， 选择排序的赋值操作介于0和3(n-1)次之间，其平均时间复杂度为O(n2)
     * 交换次数比冒泡排序少多了，由于交换所需CPU时间比比较所需的CUP时间多，所以选择排序比冒泡排序快。
     * 但是N比较大时，比较所需的CPU时间占主要地位，所以这时的性能和冒泡排序差不太多，但毫无疑问肯定要快些。
     * 
     * @since 1.1
     * @param source
     *            需要进行排序操作的数组
     * @return 排序后的数组
     */
    public static int[] selectSort(int[] source) {
        for (int i = 0; i < source.length; i++) {
            for (int j = i + 1; j < source.length; j++) {
                if (source[i] > source[j]) {
                    swap(source, i, j);
                }
            }
        }
        return source;
    }

    /**
     * 插入排序 方法：将一个记录插入到已排好序的有序表（有可能是空表）中,从而得到一个新的记录数增1的有序表。 性能：比较次数O(n^2),n^2/4
     * 复制次数O(n),n^2/4 比较次数是前两者的一般，而复制所需的CPU时间较交换少，所以性能上比冒泡排序提高一倍多，而比选择排序也要快。
     * 
     * @since 1.1
     * @param source
     *            需要进行排序操作的数组
     * @return 排序后的数组
     */
    public static int[] insertSort(int[] source) {

        for (int i = 1; i < source.length; i++) {
            for (int j = i; (j > 0) && (source[j] < source[j - 1]); j--) {
                swap(source, j, j - 1);
            }
        }
        return source;
    }

    /**
     * 快速排序 快速排序使用分治法（Divide and conquer）策略来把一个序列（list）分为两个子序列（sub-lists）。 步骤为：
     * 1. 从数列中挑出一个元素，称为 "基准"（pivot）， 2.
     * 重新排序数列，所有元素比基准值小的摆放在基准前面，所有元素比基准值大的摆在基准的后面
     * （相同的数可以到任一边）。在这个分割之后，该基准是它的最后位置。这个称为分割（partition）操作。 3.
     * 递归地（recursive）把小于基准值元素的子数列和大于基准值元素的子数列排序。
     * 递回的最底部情形，是数列的大小是零或一，也就是永远都已经被排序好了
     * 。虽然一直递回下去，但是这个算法总会结束，因为在每次的迭代（iteration）中，它至少会把一个元素摆到它最后的位置去。
     * 
     * @since 1.1
     * @param source
     *            需要进行排序操作的数组
     * @return int[] 排序后的数组
     */
    public static int[] quickSort(int[] source) {
        return qsort(source, 0, source.length - 1);
    }

    /**
     * 快速排序重载
     * 
     * @author zhuhaojie
     * @time 2016年10月9日下午6:06:42
     * @param source
     *            待排序数组
     * @return double[] 排序后的数组
     */
    public static double[] quickSort(double[] source) {
        return qsort(source, 0, source.length - 1);
    }

    /**
     *@param src 任意集合对象
     *@author：zhuhaojie 
     *@time：2017年5月31日 上午11:33:11
     *@return Object[] 对象数组
     */
    public static Object[] removeNull(List[] src) {
        List list = new ArrayList();
        int length = src.length;
        for (int i = 0; i < length; i++) {
            Object obj = src[i];
            if (obj != null) {
                list.add(obj);
            }
        }
        Object[] result = list.toArray(new List[1]);
        return result;
    }

    /**
     * 快速排序的具体实现，排正序
     * 
     * @since 1.1
     * @param source
     *            需要进行排序操作的数组
     * @param low
     *            开始低位
     * @param high
     *            结束高位
     * @return 排序后的数组
     */
    private static int[] qsort(int[] source, int low, int high) {
        int i, j, x;
        if (low < high) {
            i = low;
            j = high;
            x = source[i];
            while (i < j) {
                while (i < j && source[j] > x) {
                    j--;
                }
                if (i < j) {
                    source[i] = source[j];
                    i++;
                }
                while (i < j && source[i] < x) {
                    i++;
                }
                if (i < j) {
                    source[j] = source[i];
                    j--;
                }
            }
            source[i] = x;
            qsort(source, low, i - 1);
            qsort(source, i + 1, high);
        }
        return source;
    }

    /**
     * 快速排序的具体实现，排正序
     * 
     * @since 1.1
     * @param source
     *            需要进行排序操作的数组
     * @param low
     *            开始低位
     * @param high
     *            结束高位
     * @return 排序后的数组
     */
    private static double[] qsort(double[] source, int low, int high) {
        int i, j;
        double x;
        if (low < high) {
            i = low;
            j = high;
            x = source[i];
            while (i < j) {
                while (i < j && source[j] > x) {
                    j--;
                }
                if (i < j) {
                    source[i] = source[j];
                    i++;
                }
                while (i < j && source[i] < x) {
                    i++;
                }
                if (i < j) {
                    source[j] = source[i];
                    j--;
                }
            }
            source[i] = x;
            qsort(source, low, i - 1);
            qsort(source, i + 1, high);
        }
        return source;
    }

    /**
     * 二分法查找 查找线性表必须是有序列表
     * 
     * @since 1.1
     * @param source
     *            需要进行查找操作的数组
     * @return 需要查找的值在数组中的位置，若未查到则返回-1
     */
    public static int[] binarySearch(int[] source) {
        int i, j;
        int low, high, mid;
        int temp;
        for (i = 0; i < source.length; i++) {
            temp = source[i];
            low = 0;
            high = i - 1;
            while (low <= high) {
                mid = (low + high) / 2;
                if (source[mid] > temp) {
                    high = mid - 1;
                } else {
                    low = mid + 1;
                }
            }
            for (j = i - 1; j > high; j--)
                source[j + 1] = source[j];
            source[high + 1] = temp;
        }
        return source;
    }

    /**
     * 反转数组
     * 
     * @since 1.1
     * @param source
     *            需要进行反转操作的数组
     * @return 反转后的数组
     */
    public static int[] reverse(int[] source) {
        int length = source.length;
        int temp = 0;
        for (int i = 0; i < length >> 1; i++) {
            temp = source[i];
            source[i] = source[length - 1 - i];
            source[length - 1 - i] = temp;
        }
        return source;
    }

    /**
     * 反转数组
     * 
     * @since 1.1
     * @param source
     *            需要进行反转操作的数组
     * @return 反转后的数组
     */
    public static double[] reverse(double[] source) {
        int length = source.length;
        double temp = 0;
        for (int i = 0; i < length >> 1; i++) {
            temp = source[i];
            source[i] = source[length - 1 - i];
            source[length - 1 - i] = temp;
        }
        return source;
    }

    /**
     * 在当前位置插入一个元素,数组中原有元素向后移动; 如果插入位置超出原数组， 则抛IllegalArgumentException异常
     * 
     * @param array
     *            数组
     * @param index
     *            索引值
     * @param insertNumber
     *            插入的元素值
     * @return int[] 插入元素后的数组
     */
    public static int[] insert(int[] array, int index, int insertNumber) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException();
        }
        if (index - 1 > array.length || index <= 0) {
            throw new IllegalArgumentException();
        }
        int[] dest = new int[array.length + 1];
        System.arraycopy(array, 0, dest, 0, index - 1);
        dest[index - 1] = insertNumber;
        System.arraycopy(array, index - 1, dest, index, dest.length - index);
        return dest;
    }

    /**
     * 整形数组中特定位置删除掉一个元素, 数组中原有元素向前移动; 如果插入位置超出原数组， 则抛IllegalArgumentException异常
     * 
     * @param array
     *            数组
     * @param index
     *            索引值
     * @return int[] 删除特定元素后的数组
     */
    public static int[] remove(int[] array, int index) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException();
        }
        if (index > array.length || index <= 0) {
            throw new IllegalArgumentException();
        }
        int[] dest = new int[array.length - 1];
        System.arraycopy(array, 0, dest, 0, index - 1);
        System.arraycopy(array, index, dest, index - 1, array.length - index);
        return dest;
    }

    /**
     * 2个数组合并，形成一个新的数组
     * 
     * @param array1
     *            数组1
     * @param array2
     *            数组2
     * @return int[] 合并后的数组
     */
    public static int[] merge(int[] array1, int[] array2) {
        int[] dest = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, dest, 0, array1.length);
        System.arraycopy(array2, 0, dest, array1.length, array2.length);
        return dest;
    }

    /**
     * 数组中有n个数据，要将它们顺序循环向后移动k位， 即前面的元素向后移动k位，后面的元素则循环向前移k位，
     * 例如，0、1、2、3、4循环移动3位后为2、3、4、0、1。
     * 
     * @param array
     *            数组
     * @param offset
     *            索引
     * @return int[] 移动元素后的数组
     */
    public static int[] offsetArray(int[] array, int offset) {
        int length = array.length;
        int moveLength = length - offset;
        int[] temp = Arrays.copyOfRange(array, moveLength, length);
        System.arraycopy(array, 0, array, offset, moveLength);
        System.arraycopy(temp, 0, array, 0, offset);
        return array;
    }

    /**
     * 随机打乱一个数组
     * 
     * @param <T> 任意对象
     * 
     * @param list
     *            集合
     * @return List 打乱后的数组转换成的List
     */
    public static <T> List<T> shuffle(List<T> list) {
        java.util.Collections.shuffle(list);
        return list;
    }

    /**
     * 随机打乱一个数组
     * 
     * @param array
     *            数组
     * @return int[] 打乱后的数组
     */
    public int[] shuffle(int[] array) {
        Random random = new Random();
        for (int index = array.length - 1; index >= 0; index--) {
            // 从0到index处之间随机取一个值，跟index处的元素交换
            exchange(array, random.nextInt(index + 1), index);
        }
        return array;
    }

    /**
     * 交换数组中两个索引元素的位置
     * 
     * @author zhuhaojie
     * @time 2016年10月9日下午5:56:47
     * @param array
     *            指定数组
     * @param p1
     *            索引1
     * @param p2
     *            索引2
     */
    private void exchange(int[] array, int p1, int p2) {
        int temp = array[p1];
        array[p1] = array[p2];
        array[p2] = temp;
    }

    /**
     * 对两个有序数组进行合并,并将重复的数字将其去掉
     * 
     * @param a
     *            ：已排好序的数组a
     * @param b
     *            ：已排好序的数组b
     * @return 合并后的排序数组
     */
    private static List<Integer> mergeByList(int[] a, int[] b) {
        // 用于返回的新数组，长度可能不为a,b数组之和，因为可能有重复的数字需要去掉
        List<Integer> c = new ArrayList<Integer>();
        // a数组下标
        int aIndex = 0;
        // b数组下标
        int bIndex = 0;
        // 对a、b两数组的值进行比较，并将小的值加到c，并将该数组下标+1，
        // 如果相等，则将其任意一个加到c，两数组下标均+1
        // 如果下标超出该数组长度，则退出循环
        while (true) {
            if (aIndex > a.length - 1 || bIndex > b.length - 1) {
                break;
            }
            if (a[aIndex] < b[bIndex]) {
                c.add(a[aIndex]);
                aIndex++;
            } else if (a[aIndex] > b[bIndex]) {
                c.add(b[bIndex]);
                bIndex++;
            } else {
                c.add(a[aIndex]);
                aIndex++;
                bIndex++;
            }
        }
        // 将没有超出数组下标的数组其余全部加到数组c中
        // 如果a数组还有数字没有处理
        if (aIndex <= a.length - 1) {
            for (int i = aIndex; i <= a.length - 1; i++) {
                c.add(a[i]);
            }
            // 如果b数组中还有数字没有处理
        } else if (bIndex <= b.length - 1) {
            for (int i = bIndex; i <= b.length - 1; i++) {
                c.add(b[i]);
            }
        }
        return c;
    }

    /**
     * 对两个有序数组进行合并,并将重复的数字将其去掉
     * 
     * @param a
     *            :已排好序的数组a
     * @param b
     *            :已排好序的数组b
     * @return 合并后的排序数组,返回数组的长度=a.length + b.length,不足部分补0
     */
    @SuppressWarnings("unused")
    private static int[] mergeByArray(int[] a, int[] b) {
        int[] c = new int[a.length + b.length];

        int i = 0, j = 0, k = 0;

        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) {
                if (a[i] == b[j]) {
                    j++;
                } else {
                    c[k] = a[i];
                    i++;
                    k++;
                }
            } else {
                c[k] = b[j];
                j++;
                k++;
            }
        }
        while (i < a.length) {
            c[k] = a[i];
            k++;
            i++;
        }
        while (j < b.length) {
            c[k] = b[j];
            j++;
            k++;
        }
        return c;
    }

    /**
     * 对两个有序数组进行合并,并将重复的数字将其去掉
     * 
     * @param a
     *            ：可以是没有排序的数组
     * @param b
     *            ：可以是没有排序的数组
     * @return 合并后的排序数组 打印时可以这样： Map<Integer,Integer> map=sortByTreeMap(a,b);
     *         Iterator iterator = map.entrySet().iterator(); while
     *         (iterator.hasNext()) { Map.Entry mapentry =
     *         (Map.Entry)iterator.next(); System.out.print(mapentry.getValue()+
     *         " "); }
     */
    @SuppressWarnings("unused")
    private static Map<Integer, Integer> mergeByTreeMap(int[] a, int[] b) {
        Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
        for (int i = 0; i < a.length; i++) {
            map.put(a[i], a[i]);
        }
        for (int i = 0; i < b.length; i++) {
            map.put(b[i], b[i]);
        }
        return map;
    }

    /**
     * 在控制台打印数组，之间用逗号隔开,调试时用
     * 
     * @param array
     *            数组
     * @return String 数组元素转换成的字符串
     */
    public static String print(int[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            sb.append("," + array[i]);
        }
        System.out.println("\n" + sb.toString().substring(1));
        return sb.toString().substring(1);
    }

    /**
     * 去除重复元素
     * 
     * @author zhuhaojie
     * @time 2016年10月9日下午6:51:53
     * @param nums
     *            含有重复元素的数组
     * @return Integer[] 去除重复元素后的数组
     */
    public static int[] removeRepeat(int[] nums) {
        List<Integer> numList = new ArrayList<Integer>();
        for (int i : nums) {
            numList.add(i);
        }
        Set<Integer> numSet = new HashSet<Integer>();
        numSet.addAll(numList);
        int size = numSet.size();
        Integer[] results = numSet.toArray(new Integer[size]);

        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = results[i].intValue();
        }
        return result;
    }

    /**
     * 去除重复元素
     * 
     * @author zhuhaojie
     * @time 2016年10月9日下午6:51:53
     * @param nums
     *            含有重复元素的数组
     * @return String[] 去除重复元素后的数组
     */
    public static String[] removeRepeat(String[] nums) {
        if (nums == null) {
            return null;
        }
        List<String> numList = new ArrayList<String>();
        for (String i : nums) {
            numList.add(i);
        }
        Set<String> numSet = new HashSet<String>();
        numSet.addAll(numList);
        int size = numSet.size();
        String[] results = numSet.toArray(new String[size]);
        return results;
    }

    /**
     * 是否含有重复元素
     * @param nums 字符串型数组
     * @author：zhuhaojie
     * @time：2017年5月30日 下午3:48:27
     * @return boolean true:有重复元素
     *                 false：没有重复元素
     */
    public static boolean hasRepeat(String[] nums) {
        if (nums == null || nums.length == 0) {
            return false;
        }
        boolean flag = false;
        int length = nums.length;
        for (int i = 0; i < length; i++) {
            String temp = nums[i];
            int count = 0;
            for (int j = 0; j < length; j++) {
                String temp2 = nums[j];
                if (temp == null) {
                    if (temp == temp2) {
                        count++;
                    }
                } else {
                    temp = temp.trim();
                    if (temp2 != null) {
                        temp2 = temp2.trim();
                        if (temp.equals(temp2)) {
                            count++;
                        }
                    }
                }
            }
            // 由于中间又一次会跟自己本身比较所有这里要判断count>=2
            if (count >= 2) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 判断subset数组是否是compleate的子集
     * @param compleate 全集数组
     * @param subset 子集数组
     * @author：zhuhaojie
     * @time：2017年5月30日 下午4:10:09
     * @return boolean true:subset是compleate的子集，false:subset不是compleate的子集
     */
    public static boolean aisBSubset(String[] compleate, String[] subset) {

        // 生成不变List
        List<String> listA = Arrays.asList(compleate);
        List<String> listB = Arrays.asList(subset);
        boolean result = listA.containsAll(listB);
        return result;
    }

    /**
     * 去除重复元素
     * 
     * @author zhuhaojie
     * @time 2016年10月9日下午6:51:53
     * @param nums
     *            含有重复元素的数组
     * @return Integer[] 去除重复元素后的数组
     */
    public static double[] removeRepeat(double[] nums) {
        List<Double> numList = new ArrayList<Double>();
        for (double i : nums) {
            numList.add(i);
        }
        Set<Double> numSet = new HashSet<Double>();
        numSet.addAll(numList);
        int size = numSet.size();
        Double[] results = numSet.toArray(new Double[size]);

        double[] result = new double[size];
        for (int i = 0; i < size; i++) {
            result[i] = results[i].doubleValue();
        }
        return result;
    }

    /**
     * 删除指定元素
     * 
     * @author zhuhaojie
     * @time 2016年10月9日下午7:05:17
     * @param arr
     *            数组
     * @param a
     *            指定元素
     * @return int[] 删除指定元素后的数组
     */
    public static int[] deleteOne(int[] arr, int a) {
        if (arr == null) {
            return null;
        }
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != a) {
                list.add(arr[i]);
            }
        }
        int size = list.size();
        Integer[] result = list.toArray(new Integer[size]);
        int[] results = new int[size];
        for (int i = 0; i < size; i++) {
            results[i] = result[i].intValue();
        }
        return results;
    }
    /**
     *判断数组中是否全部为指定元素
     *@param source 指定数组
     *@param e 指定元素
     *@return boolean true:数组source中全部为e,false:数组中不全部为e
     *@author：zhuhaojie 
     *@time：2017年8月23日 上午9:07:45
     */
    public static boolean sameElement(double[] source, double e) {
        boolean t = true;
        int length = source.length;
        for (int  i = 0; i < length; i++) {
            if (e != source[i]) {
                t = false;
                break;
            }
        }
        return t;
    }

    /**
     * 反转数组元素
     * 
     * @author zhuhaojie
     * @time 2016年10月13日下午6:16:33
     * @param array
     *            数组
     * @return String[] 反转后的数组
     */
    public static String[] reverseArray2(String[] array) {
        int length = array.length;
        String[] new_array = new String[length];
        for (int i = 0; i < length; i++) {
            // 反转后数组的第一个元素等于源数组的最后一个元素：
            new_array[i] = array[length - i - 1];
        }
        return new_array;
    }

    /**
     * 删除指定元素
     * 
     * @author zhuhaojie
     * @time 2016年10月9日下午7:05:17
     * @param arr
     *            数组
     * @param a
     *            指定元素
     * @return String[] 删除指定元素后的数组
     */
    public static String[] deleteOne(String[] arr, String a) {

        if (arr == null) {
            return null;
        }
        ArrayList<String> list = new ArrayList<String>();

        if (a != null) {
            a = a.trim();
        }

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null) {
                arr[i] = arr[i].trim();
            }
            if (a == null) {
                if (a != arr[i]) {
                    list.add(arr[i]);
                }
            } else {
                if (!a.equals(arr[i])) {
                    list.add(arr[i]);
                }
            }
        }
        int size = list.size();
        String[] result = list.toArray(new String[size]);
        return result;
    }

    /**
     * 向指定数组的指定索引，添加一个元素
     * 
     * @author zhuhaojie
     * @time 2016年11月30日下午2:15:26
     * @param array
     *            原始数组
     * @param index
     *            要插入到的索引值
     * @param s
     *            要插入的元素
     * @return String[] 添加元素后的数组
     *
     */
    public static String[] addEelement(String[] array, int index, String s) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("索引不能小于0,index:" + index);
        }
        List<String> list = new ArrayList<String>();
        int length = array.length;
        for (int i = 0; i < length; i++) {
            list.add(array[i]);
        }
        list.add(index, s);
        String[] newStr = list.toArray(new String[1]);
        return newStr;
    }

    /**
     * 将指定数组的指定索引处元素删除
     * 
     * @author zhuhaojie
     * @time 2016年11月30日下午2:22:22
     * @param array
     *            原始数组
     * @param index
     *            要删除的元素的索引
     * @return String[] 删除此元素后的数组
     *
     */
    public static String[] deleteElement(String[] array, int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("索引不能小于0,index:" + index);
        }
        List<String> list = new ArrayList<String>();
        int length = array.length;
        for (int i = 0; i < length; i++) {
            list.add(array[i]);
        }
        list.remove(index);
        String[] newStr = list.toArray(new String[1]);
        return newStr;
    }

    /**
     * 将double类型数组转换成字符串类型数组
     * 
     * @author zhuhaojie
     * @time 2016年11月30日下午4:21:58
     * @param dou
     *            double类型数组
     * @param index
     *            转换后拼接在值后面的字符串 可以为null
     * 
     * @return String[] 字符串类型数组
     *
     */
    public static String[] doubleToString(double[] dou, String index) {
        if (dou == null) {
            return null;
        }
        int leng = dou.length;
        if (leng == 0) {
            return new String[] {};
        }
        String[] result = new String[leng];
        if (index != null && !index.trim().equals("")) {
            for (int i = 0; i < leng; i++) {
                result[i] = dou[i] + index;
            }
        } else {
            for (int i = 0; i < leng; i++) {
                result[i] = dou[i] + "";
            }
        }

        return result;
    }

    /**
     * 替换元素
     * 
     * @param array
     *            原始数组
     * @param newElement
     *            要替换的新元素
     * @param old
     *            原始元素
     * @return 替换后的数组
     */

    public static String[] replaceElement(String[] array, String newElement, String old) {
        if (array == null) {
            return array;
        }
        int length = array.length;
        for (int i = 0; i < length; i++) {
            String str = array[i];
            if (str != null) {
                str = str.trim();
                if (str.equals(old)) {
                    array[i] = newElement;
                }
            } else {
                if (old == null) {
                    array[i] = newElement;
                }
            }
        }
        return array;
    }
    /**
     * 将字节型数组转换成加密后的字符串
     * @param arr 待加密的字节数组
     * @return String 加密后的字符串
     *@author：zhuhaojie 
     *@time：2017年5月31日 上午11:36:02
     */
    public static String byteToHexString(byte[] arr) {
        StringBuffer sb = new StringBuffer();
        String head = "00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15    ";
        sb.append(head + "text(" + arr.length + ")\n");
        sb.append("-------------------------------------------------------\n");
        StringBuffer chars = new StringBuffer(arr.length * 2);
        int n = 0;
        for (int i = 0; i < arr.length; i++) {
            String hex = Integer.toHexString(0xFF & arr[i]).toUpperCase();
            char asc = (char) arr[i];
            if (asc == '\r' || asc == '\n') {
                asc = '.';
            }
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            sb.append(hex).append(' ');
            chars.append(asc).append(' ');
            if ((i + 1) % 16 == 0) {
                sb.append("   " + chars.toString());
                sb.append('\n');
                chars = new StringBuffer();
                n = 0;
            }
            n++;
        }
        if (n != 0) {
            for (; n != 16; n++) {
                chars.insert(0, "   ");
            }
            chars.insert(0, "      ");
        }
        sb.append(chars.toString());
        sb.append('\n');
        return sb.toString();

    }
    
    /**
     * 将数组转换成List
     * 
     * @param array
     *            数组
     * @param <T>
     *            泛型对象
     * @return List<T> 转换后的List对象
     * @author：zhuhaojie
     * @time：2017年8月1日 上午10:09:38
     */
    public static <T> List<T> converArrayToList(T[] array) {
        List<T> list = new ArrayList<T>();
        Collections.addAll(list, array);
        return list;
    }

//    public static void main(String[] args) {
//
//        boolean t = aisBSubset(null, null);
//        System.out.println("t:" + t);
//    }

}