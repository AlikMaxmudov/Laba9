package task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class task6 {

    public static void main(String[] args) {
        System.out.println("----1----");
        System.out.println(bell(1));
        System.out.println(bell(2));
        System.out.println(bell(3));
        System.out.println("----2----");
        System.out.println(translateWord("flag"));
        System.out.println(translateWord("Apple"));
        System.out.println(translateWord("button"));
        System.out.println(translateWord(""));
        System.out.println(translateSentence("I like to eat honey waffles."));
        System.out.println(translateSentence("Do you think it is going to rain today?"));
        System.out.println("----3----");
        System.out.println(validColor("rgb(0,0,0)"));
        System.out.println(validColor("rgb(0,,0)"));
        System.out.println(validColor("rgb(255,256,255)"));
        System.out.println(validColor("rgba(0,0,0,0.123456789)"));
        System.out.println("----4----");
        System.out.println(stripUrlParams("https://edabit.com?a=1&b=2&a=2"));
        System.out.println(stripUrlParams("https://edabit.com?a=1&b=2&a=2", new String[]{"b"}));
        System.out.println(stripUrlParams("https://edabit.com", new String[]{"b"}));
        System.out.println("---5---");
        System.out.println(Arrays.toString(getHashTags("How the Avocado Became the Fruit of the Global Trade")));
        System.out.println(Arrays.toString(getHashTags("Why You Will Probably Pay More for Your Christmas Tree This Year")));
        System.out.println(Arrays.toString(getHashTags("Hey Parents, Surprise, Fruit Juice Is Not Fruit")));
        System.out.println(Arrays.toString(getHashTags("Visualizing Science")));
        System.out.println("---6---");
        System.out.println(ulam(4));
        System.out.println(ulam(9));
        System.out.println(ulam(206));
        System.out.println("---8---");
        System.out.println(convertToRoman(2));
        System.out.println(convertToRoman(12));
        System.out.println(convertToRoman(16));
        System.out.println("---9---");
        System.out.println(formula("6 * 4 = 24"));
        System.out.println(formula("18 / 7 = 2"));
        System.out.println(formula("16 * 10 = 160 = 14 + 120"));
        System.out.println(formula("16 * 10 = 160 = 40 + 120"));
        System.out.println("---10---");
        System.out.println(palindromeDescendant(11211230));
        System.out.println(palindromeDescendant(13001120));
        System.out.println(palindromeDescendant(23336014));
        System.out.println(palindromeDescendant(11));
    }

    /**
     * 1 задание
     *
     * @return число Белла - количество способов которыми массив из n элементов может
     * быть разбит на непустые подмножества
     */
    private static int bell(int o) {
        ArrayList<ArrayList<Integer>> bellTriangle = new ArrayList<>(o);
        {
            ArrayList<Integer> firstArray = new ArrayList<>(1);
            firstArray.add(1);
            bellTriangle.add(firstArray);
        }
        for (int i = 1; i < o; i++) {
            ArrayList<Integer> newArray = new ArrayList<>(i + 1);
            ArrayList<Integer> lastArray = bellTriangle.get(bellTriangle.size() - 1);
            newArray.add(lastArray.get(lastArray.size() - 1));
            for (int k = 0; k < i; k++) {
                newArray.add(lastArray.get(k) + newArray.get(k));
            }
            bellTriangle.add(newArray);
        }
        ArrayList<Integer> lastRow = bellTriangle.get(bellTriangle.size() - 1);
        return lastRow.get(lastRow.size() - 1);
    }

    /**
     * 2 задание:
     *
     * @return перевод слова с английского на свинячий латинский по правилам:
     * – Если слово начинается с согласного, переместите первую букву (буквы) слова до
     * гласного до конца слова и добавьте «ay» в конец
     * - Если слово начинается с гласной, добавьте "yay" в конце слова
     */
    private static String translateWord(String s) {
        final String vowels = "aeiou";
        Pattern pattern = Pattern.compile("\\w+");
        if (!pattern.matcher(s).matches()) {
            return "";
        }
        if (vowels.indexOf(Character.toLowerCase(s.charAt(0))) != -1) {
            return s + "yay";
        }
        Pattern pattern1 = Pattern.compile("[aeiou]");
        Matcher matcher = pattern1.matcher(s);
        int indexOfVowel = -1;
        if (matcher.find()) {
            indexOfVowel = matcher.start();
        }
        if (indexOfVowel == -1) {
            return s + "ay";
        }
        String translated = s.substring(indexOfVowel) + s.substring(0, indexOfVowel) + "ay";
        if (Character.isUpperCase(s.charAt(0))) {
            return translated.substring(0, 1).toUpperCase(Locale.ROOT) + translated.substring(1).toLowerCase(Locale.ROOT);
        } else {
            return translated;
        }
    }

    /**
     * 2 задание:
     *
     * @return перевести на свинячий латинский предложения по правилам из translateWord
     */
    private static String translateSentence(String s) {
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(s);
        return matcher.replaceAll(matchResult -> translateWord(matchResult.group()));
    }

    /**
     * 3 задача
     *
     * @return true если строка соответствует формату:
     * rgb(r,g,b) (r,g,b принадлежат [0,255] и являются целочисленными)
     * или rgba(r,g,b,a) (a принадлежит [0,1])
     */
    private static boolean validColor(String s) {
        Integer r = null;
        Integer g = null;
        Integer b = null;
        Double a = null;
        Pattern pattern = Pattern.compile("\\d+\\.?\\d*");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            try {
                if (r == null) {
                    r = Integer.parseInt(matcher.group());
                } else if (g == null) {
                    g = Integer.parseInt(matcher.group());
                } else if (b == null) {
                    b = Integer.parseInt(matcher.group());
                } else if (a == null) {
                    a = Double.parseDouble(matcher.group());
                }
            } catch (Exception e) {
                return false;
            }
        }
        if (r == null || r > 255 || r < 0) {
            return false;
        }
        if (g == null || g > 255 || g < 0) {
            return false;
        }
        if (b == null || b > 255 || b < 0) {
            return false;
        }
        if (s.contains("rgba")) {
            return a != null && a <= 1 && a >= 0;
        } else return a == null && s.contains("rgb");
    }

    /**
     * 4 задание:
     * из url запроса убрать дублирующиеся параметры, и удалить указанные в paramsToDelete
     */
    private static String stripUrlParams(String url, String[] paramsToDelete) {
        String[] urlAndParamPair = url.split("\\?");
        if (urlAndParamPair.length == 1) {
            return url;
        }
        String[] params = urlAndParamPair[1].split("&");

        Map<String, String> paramsAndValuesMap = new HashMap<>();
        for (String param : params) {
            String[] keyAndValue = param.split("=");
            boolean shouldBeAdded = true;
            for (String paramToDelete : paramsToDelete) {
                if (keyAndValue[0].equals(paramToDelete)) {
                    shouldBeAdded = false;
                    break;
                }
            }
            if (shouldBeAdded) {
                paramsAndValuesMap.put(keyAndValue[0], keyAndValue[1]);
            }
        }

        StringJoiner joiner = new StringJoiner("&");
        for (Map.Entry<String, String> entry : paramsAndValuesMap.entrySet()) {
            joiner.add(entry.getKey() + "=" + entry.getValue());
        }
        return urlAndParamPair[0] + "?" + joiner;
    }

    /**
     * 4 задание:
     * перегрузка stripUrlParams но с отсутствием параметров к удалению
     */
    private static String stripUrlParams(String url) {
        return stripUrlParams(url, new String[]{});
    }

    /**
     * 5 задание
     *
     * @return 3 (или менее, если в строке меньше 3 слов) самых длинных слов из строки.
     * Если есть несколько слов одинаковой длины, берется самое первое из них, остальные игнорируются.
     */
    private static String[] getHashTags(String s) {
        ArrayList<String> words = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\w+");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            words.add(matcher.group());
        }

        String[] longestWords = new String[]{null, null, null};
        final int maxIterator = 0;
        final int secondIterator = 1;
        final int thirdIterator = 2;
        for (String word : words) {
            if (longestWords[maxIterator] == null || longestWords[maxIterator].length() < word.length()) {
                longestWords[thirdIterator] = longestWords[secondIterator];
                longestWords[secondIterator] = longestWords[maxIterator];
                longestWords[maxIterator] = word;
            } else if (longestWords[secondIterator] == null || longestWords[secondIterator].length() < word.length()) {
                longestWords[thirdIterator] = longestWords[secondIterator];
                longestWords[secondIterator] = word;
            } else if (longestWords[thirdIterator] == null || longestWords[thirdIterator].length() < word.length()) {
                longestWords[thirdIterator] = word;
            }
        }
        ArrayList<String> hashTags = new ArrayList<>(3);
        for (String longestWord : longestWords) {
            if (longestWord != null) {
                hashTags.add("#" + longestWord.toLowerCase(Locale.ROOT));
            }
        }
        return hashTags.toArray(new String[]{});
    }


    /**
     * 6 задание:
     *
     * @return n-ное число в последовательности улама
     */
    private static int ulam(int n) {
        Map<Integer, Integer> savedSums = new TreeMap<>();
        ArrayList<Integer> ulams = new ArrayList<>();
        ulams.add(1);
        ulams.add(2);
        ulams.add(3);
        for (int i = 2; i < n - 1; i++) {
            Integer newSum = null;
            for (int k = 0; k < i; k++) {
                int currentSum = ulams.get(ulams.size() - 1) + ulams.get(k);
                if (newSum == null && !savedSums.containsKey(currentSum)) {
                    newSum = currentSum;
                }
                savedSums.merge(currentSum, 1, Integer::sum);
            }
            for (Map.Entry<Integer, Integer> en : savedSums.entrySet()) {
                if (newSum > en.getKey() && en.getValue() == 1) {
                    newSum = en.getKey();
                    break;
                }
            }
            ulams.add(newSum);
            savedSums.remove(newSum);
        }
        return ulams.get(n - 1);
    }

    /**
     * 8 задание:
     *
     * @return арабскую цифру в римском представлении
     */
    private static String convertToRoman(int n) {

        Roum[] roums = Roum.values();
        StringBuilder builder = new StringBuilder();
        for (Roum roum : roums) {
            if (n - roum.arab >= 0) {
                builder.append(roum.name());
                n -= roum.arab;
            }
        }
        return builder.toString();
    }

    /**
     * 9 задание:
     *
     * @return true если данное уравнение верное
     * работает с уравениями типа A operator B = C operator D = X..., где A,B,C,D,X - константы
     * возможные операторы: +, -, *, /. Можно ставить несколько операторов подряд, например A * B * C.
     * При этом правильный порядок действия не соблюдается (в задании не сказано добавить расставку скобок)
     * Допустимо наличие отсутствие оператора, наличие нескольких частей уравнения.
     * Работает только с целыми положительными числами
     */
    private static boolean formula(String s) {

        String[] expressions = s.split("=");


        Pattern patternDigit = Pattern.compile("\\d+");
        Matcher matcherDigit = patternDigit.matcher("");

        Pattern patternOperators = Pattern.compile("[*/+-]");
        Matcher matcherOperators = patternOperators.matcher("");
        Double lastResult = null;

        for (String expr : expressions) {
            ArrayList<Integer> digits = new ArrayList<>();
            ArrayList<Character> operators = new ArrayList<>();
            matcherOperators.reset(expr);
            matcherDigit.reset(expr);
            while (matcherDigit.find()) {
                digits.add(Integer.parseInt(matcherDigit.group()));
            }
            while (matcherOperators.find()) {
                operators.add(matcherOperators.group().charAt(0));
            }
            if (digits.size() != operators.size() + 1) {
                return false;
            }

            double currentDouble = digits.get(0);
            for (int i = 0; i < operators.size(); i++) {
                int newInt = digits.get(i + 1);
                switch (operators.get(i)) {
                    case '*': {
                        currentDouble *= newInt;
                        break;
                    }
                    case '/': {
                        currentDouble /= newInt;
                        break;
                    }
                    case '+': {
                        currentDouble += newInt;
                        break;
                    }
                    default: {
                        currentDouble -= newInt;
                        break;
                    }
                }
            }
            if (lastResult != null && currentDouble != lastResult) {
                return false;
            }
            lastResult = currentDouble;
        }
        return true;
    }

    /**
     * 10 задание:
     *
     * @return является ли данная строка палиндромом и можно ли путем сложения каждой соседней цифры создать палиндром
     */
    private static boolean palindromeDescendant(int number) {
        if (number < 10) {
            return true;
        }

        while (number > 10) {
            String numberStr = "" + number;
            String reversedStr = new StringBuilder(numberStr).reverse().toString();
            if (numberStr.equals(reversedStr)) {
                return true;
            }
            if (numberStr.length() % 2 != 0) {
                return false;
            }
            StringBuilder newStrBuilder = new StringBuilder();
            for (int i = 0; i <= numberStr.length() - 2; i += 2) {
                newStrBuilder.append(Integer.parseInt(numberStr.substring(i, i + 1)) + Integer.parseInt(numberStr.substring(i + 1, i + 2)));
            }
            number = Integer.parseInt(newStrBuilder.toString());
        }
        return false;
    }

    private enum Roum {
        MMM(3000),
        MM(2000),
        M(1000),
        CM(900),
        DCCC(800),
        DCC(700),
        DC(600),
        D(500),
        CD(400),
        CCC(300),
        CC(200),
        C(100),
        XC(90),
        LXXX(80),
        LXX(70),
        LX(60),
        L(50),
        XL(40),
        XXX(30),
        XX(20),
        X(10),
        IX(9),
        VIII(8),
        VII(7),
        VI(6),
        V(5),
        IV(4),
        III(3),
        II(2),
        I(1);

        final int arab;

        Roum(int arab) {
            this.arab = arab;
        }
    }
}