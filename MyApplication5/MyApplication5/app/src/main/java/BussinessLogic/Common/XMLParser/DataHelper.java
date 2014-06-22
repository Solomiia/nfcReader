package BussinessLogic.Common.XMLParser;

import java.util.HashMap;

/**
 * Created by Solomiia on 6/13/2014.
 */
public class DataHelper {

    HashMap<String, String> ukrainianChars;

    public DataHelper() {
        ukrainianChars = new HashMap<String, String>();
        ukrainianChars.put("і","i");
        ukrainianChars.put("ї","i");

       //todo: check other letters

    }


    public String ChangeUkrainianLettersToEnglish(String word)
    {
        if (word == null || word == "") return "";
        String data = "";

        for (String letter: ukrainianChars.keySet()) {
            if (word.contains(letter))
            {
                data = word.replace(letter, ukrainianChars.get(letter));
            }
        }
        return data;
    }
}
