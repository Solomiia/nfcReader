package Models;

import java.util.List;

/**
 * Created by Solomiia on 4/10/2014.
 */
public class TagInformation {

    public int Identifier;
    public String ImagePath;
    public String Text;
    public List<TechTypes> TechTypes;
    public TagDetails TagDetails;
    public String Type;
    public NdefMessageInformation ndefMessageInformation;
}
