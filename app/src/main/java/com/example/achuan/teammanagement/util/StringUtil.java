package com.example.achuan.teammanagement.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by achuan on 16-11-8.
 * 功能：对字符和文字进行处理的工具类
 */
public class StringUtil {
    /**
     * 如果字符串的首字符为汉字，则返回该汉字的拼音大写首字母
     * 如果字符串的首字符为字母，也转化为大写字母返回
     * 其他情况均返回' '
     *
     * @param str 字符串
     * @return 首字母
     */
    public static char getHeadChar(String str) {
        if (str != null && str.trim().length() != 0) {
            char[] strChar = str.toCharArray();
            char headChar = strChar[0];
            //如果是大写字母则直接返回
            if (Character.isUpperCase(headChar)) {
                return headChar;
            } else if (Character.isLowerCase(headChar)) {
                return Character.toUpperCase(headChar);
            }
            // 汉语拼音格式输出类
            HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
            hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
            hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            if (String.valueOf(headChar).matches("[\\u4E00-\\u9FA5]+")) {
                try {
                    String[] stringArray = PinyinHelper.toHanyuPinyinStringArray(headChar, hanYuPinOutputFormat);
                    if (stringArray != null && stringArray[0] != null) {
                        return stringArray[0].charAt(0);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    return '#';
                }
            }
        }
        return '#';
    }

    /**
     * 验证手机号码
     * @param phoneNumber 手机号码
     * @return boolean
     * 验证手机号正则表达式
       现在的号段实在太多了，什么都有，那就放宽点要求呗
       以1开头的11位数字组合
     */
    public static boolean checkPhoneNumber(String phoneNumber){
        Pattern pattern= Pattern.compile("^1[0-9]{10}$");
        Matcher matcher=pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    /**
     * 验证用户名
     * @param username 用户名
     * @return boolean
     * 验证用户名，如6到12位字母数字组合
     */
    public static boolean checkUsername(String username){
        String regex = "([a-zA-Z0-9]{6,12})";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(username);
        return m.matches();
    }

    /**
     * 通过日期来确定星座
     * @param mouth
     * @param day
     * @return
     */
    public static String getStarSeat(int mouth, int day) {
        String starSeat = null;

        if ((mouth == 3 && day >= 21) || (mouth == 4 && day <= 19)) {
            starSeat = "白羊座";
        } else if ((mouth == 4 && day >= 20) || (mouth == 5 && day <= 20)) {
            starSeat = "金牛座";
        } else if ((mouth == 5 && day >= 21) || (mouth == 6 && day <= 21)) {
            starSeat = "双子座";
        } else if ((mouth == 6 && day >= 22) || (mouth == 7 && day <= 22)) {
            starSeat = "巨蟹座";
        } else if ((mouth == 7 && day >= 23) || (mouth == 8 && day <= 22)) {
            starSeat = "狮子座";
        } else if ((mouth == 8 && day >= 23) || (mouth == 9 && day <= 22)) {
            starSeat = "处女座";
        } else if ((mouth == 9 && day >= 23) || (mouth == 10 && day <= 23)) {
            starSeat = "天秤座";
        } else if ((mouth == 10 && day >= 24) || (mouth == 11 && day <= 22)) {
            starSeat = "天蝎座";
        } else if ((mouth == 11 && day >= 23) || (mouth == 12 && day <= 21)) {
            starSeat = "射手座";
        } else if ((mouth == 12 && day >= 22) || (mouth == 1 && day <= 19)) {
            starSeat = "摩羯座";
        } else if ((mouth == 1 && day >= 20) || (mouth == 2 && day <= 18)) {
            starSeat = "水瓶座";
        } else {
            starSeat = "双鱼座";
        }
        return starSeat;
    }

}
