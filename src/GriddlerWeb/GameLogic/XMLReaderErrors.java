package GriddlerWeb.GameLogic;

import java.util.ArrayList;
import java.util.List;

public class XMLReaderErrors{

    private ArrayList<String> errorMessage;

    public XMLReaderErrors()
    {
        errorMessage = new ArrayList<String>();
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage.add(errorMessage);
    }

    public int amountOFErrors() {return errorMessage.size();}

    @Override
    public String toString() {
        StringBuilder toRetturn = new StringBuilder();
        toRetturn.append("The file containts the following errors:" + System.lineSeparator());
        for(String currError : errorMessage)
        {
            toRetturn.append(currError +","+  System.lineSeparator());
        }
        toRetturn.append("Please open another file to continue");
        return toRetturn.toString();
    }
}
