
package threads;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Validations {


    // First method of url validation
    public static boolean isValid(String url) throws URISyntaxException{

        try {
            new URL(url).toURI();
            return true;
        }

        catch (IOException ex) {
            return false;
        }
    }

    // Second method of url validation
    public static boolean isValid2(String url)
    {
        try {
            Document tempDoc=null;
            tempDoc = (Document) Jsoup.connect(url).get();
            new URL(url).toURI();
            return true;
        }

        catch (Exception ex) {
            return false;
        }
    }

    // Method to get the link's domain
    public static String getDomain(String FatherLink){
        String domain = "";
        int NODashes = 0;
        int index = 0;
        char holdLetter;
        while(NODashes < 3){
            holdLetter=FatherLink.charAt(index);
            if(holdLetter == '/'){
                NODashes++;
                if(NODashes == 3){
                    continue;
                }

            }
            domain = domain + holdLetter;
            index++;
        }
        return domain;
    }

    // Method combines domain with link. Used to concatenate " https:// "
    public static String combine(String domain,String link){
        String result;
        result = domain + link;
        return result;
    }


}
    
