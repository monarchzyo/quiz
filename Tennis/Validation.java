package Tennis;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextField;
public class Validation 
{   
    public static boolean isDouble(String fieldValue)
    {
        Pattern pat = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher mat = pat.matcher(fieldValue);
        return mat.matches();
    }
    public static boolean isDouble(String fieldValue, double lower, double upper)
    {
        boolean result = true;
        Pattern pat = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher mat = pat.matcher(fieldValue);
        if(mat.matches())
        {
            try
            {
                //check range
                double num = Double.parseDouble(fieldValue);
                if(num < lower || num > upper)
                    result = false;
            }
            catch(NumberFormatException ex)
            {
                //something went wrong
                result = false;
            }
        }
        else
        {
            result = false;
        }
        return result;
        //return mat.matches();
    }
    public static boolean isInteger(String fieldValue)
    {
        Pattern pat = Pattern.compile("\\d+");
        Matcher mat = pat.matcher(fieldValue);
        return mat.matches();   
    }
    public static boolean isInteger(String fieldValue, int lower, int upper)
    {
        boolean result = true;
        Pattern pat = Pattern.compile("\\d+");
        Matcher mat = pat.matcher(fieldValue);
        if(mat.matches())
        {
            try
            {
                //check range
                int num = Integer.parseInt(fieldValue);
                if(num < lower || num > upper)
                    result = false;
            }
            catch(NumberFormatException ex)
            {
                //something went wrong
                result = false;
            }
        }
        else
        {
            result = false;
        }
        return result;           
    }
    public static boolean isEmpty(JTextField fieldValue)
    {
        String input = fieldValue.getText();
        if(input.length() <= 0 || input.equals(""))
            return true;
        else
            return false;
    }
    public static boolean isValidName(String input)
    {
        final short MAX_LENGTH = 40;
        final short MIN_LENGTH = 2;
        boolean result = true;
        if(input.equals("") || input.length() <= MIN_LENGTH || 
                input.length() > MAX_LENGTH || input.equals(null))
            return false;
        //Lettera and spaces in unicode only
        //String regx = "\\^[a-zA-Z '.-]*$"; //"\\^[a-zA-Z '.-]{3,30}$";      
        //String regx = "\\[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$";
        String regx =  "^[\\p{L} _.'-]+$";     //"^\\p{Lu}\\p{L}*(?:[\\s-]\\p{Lu}\\p{L}*)*$";
        Pattern pat = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher mat = pat.matcher(input);    
        result = mat.matches();
        return result;
    }
    public static boolean isValidName(String input, int lower, int upper)
    {
        boolean result = true;
        if(input.equals("") || input.length() <= lower || 
                input.length() > upper || input.equals(null))
            return false;
        //Lettera and spaces in unicode only
        //String regx = "\\^[a-zA-Z '.-]*$"; //"\\^[a-zA-Z '.-]{3,30}$";      
        //String regx = "\\[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$";
        String regx =  "^[\\p{L} _.'-]+$";     //"^\\p{Lu}\\p{L}*(?:[\\s-]\\p{Lu}\\p{L}*)*$";
        Pattern pat = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher mat = pat.matcher(input);    
        result = mat.matches();
        return result;
    }
    public static boolean isValidPhone(String fieldValue)
    {
        String regex = "^\\(?([0-9]{3})\\)?[-.\\s]?([0-9]{3})[-.\\s]?([0-9]{4})$";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(fieldValue);
        return mat.matches();
    }
    public static boolean isValidEmail(String fieldValue)
    {
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(fieldValue);
        return mat.matches();
    }
    public static boolean isValidURL(String fieldValue)
    {
        //String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        //String regex =  "^(https?:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$";
        //String regex =  "(?i)^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!(?:10|127)(?:\\.\\d{1,3}){3})(?!(?:169\\.254|192\\.168)(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))\\.?)(?::\\d{2,5})?(?:[/?#]\\S*)?$";
        //String regex =  "^(https?:\\\\/\\\\/)?(www\\.)?([\\\\w]+\\\\.)+[‌​\\\\w]{2,63}\\\\/?$";
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern pat = Pattern.compile(regex);
        Matcher mat = pat.matcher(fieldValue);
        return mat.matches();
    }
}
   

