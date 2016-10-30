package GriddlerWeb.GameLogic;

import GriddlerWeb.jaxb.schema.generated.GameDescriptor;
import GriddlerWeb.jaxb.schema.generated.Slice;
import GriddlerWeb.jaxb.schema.generated.Square;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class XmlManager
{
    private final static int   MIN_SIZE_BOARD = 10, MAX_SIZE_BOARD = 100;

    private GameDescriptor m_GameData;
    public XMLReaderErrors m_ExceptionOcurred;

    public XmlManager()
    {
        m_GameData = new GameDescriptor();
    }

    public boolean loadXML(InputStream i_PathFile)
    {
        boolean returnValue = true;
        m_ExceptionOcurred = new XMLReaderErrors();
        try {
            returnValue= getAndCheckFileXml(i_PathFile);
            if(returnValue && !(checkDataEnteredFromXML()))
            {
                returnValue = false;
            }
        }
        catch (Exception e) {
            m_ExceptionOcurred.setErrorMessage(e.getMessage());
            returnValue = false;
        }
        return returnValue;
    }

    private boolean getAndCheckFileXml(InputStream i_PathFile)
    {
        boolean retVal = true;
        try {
            retVal= fromXmlFileToObject(i_PathFile);
        }
        catch (Exception e){
            m_ExceptionOcurred.setErrorMessage("File Open error");
            retVal =false;
        }
        finally {
            return retVal;
        }

    }

    private  boolean fromXmlFileToObject(InputStream i_FiletoGetXml )
    {
        boolean retVal = true;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(GameDescriptor.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            m_GameData = (GameDescriptor) jaxbUnmarshaller.unmarshal(i_FiletoGetXml);
        } catch (JAXBException e) {
            m_ExceptionOcurred.setErrorMessage("Jaxb Exception");
            retVal =false;
        }
        finally {
            return retVal;
        }
    }
    private boolean checkDataEnteredFromXML() {
        boolean returnValue;
        String gameType = m_GameData.getGameType();
        switch (gameType) {
            case "DynamicMultiPlayers": {
                returnValue = checkBoardDataFromXml() && checkDynamicMultyPlyrsXml();
            }
            break;
            default: {
                m_ExceptionOcurred.setErrorMessage("Game Type isn't correct");
                returnValue = false;
            }
        }
        return returnValue;
    }

    private boolean checkDynamicMultyPlyrsXml()
    {
        String gameTitle = m_GameData.getDynamicMultiPlayers().getGametitle();
        String amoutPlayers = m_GameData.getDynamicMultiPlayers().getTotalPlayers();
        String totalMoves = m_GameData.getDynamicMultiPlayers().getTotalmoves();
        String plyError = "Amount of players incorrect",movesError="Amount of moves Incorrect",titleError = "Game title incorrect";
        boolean retValue = true;

        if(gameTitle.equals(null) || gameTitle.equals(""))
        {
            m_ExceptionOcurred.setErrorMessage(titleError);
            retValue= false;
        }
        if(!(checkIntegerParseAndPositive(amoutPlayers,plyError) && checkIntegerParseAndPositive(totalMoves,movesError)))
        {
            retValue = false;
        }
        return  retValue;
    }

    private boolean checkIntegerParseAndPositive(String i_ToCheck,String i_MessageError)
    {
        boolean retValue = true;
        try {
            int checked = Integer.parseInt(i_ToCheck);
            if(checked <=  0)
            {
                retValue = false;
            }
        }
        catch (Exception e){
            retValue = false;
        }
        finally {
            if(!retValue){
                m_ExceptionOcurred.setErrorMessage(i_MessageError);
            }
            return retValue;
        }
    }

    private boolean checkBoardDataFromXml()
    {
        return (checkDefinitionFromXML()&& checkSolutionFromXML());
    }

    private boolean checkSolutionFromXML()
    {
        boolean rightData = true;
        try {
            int rows = m_GameData.getBoard().getDefinition().getRows().intValue();
            int cols = m_GameData.getBoard().getDefinition().getColumns().intValue();
            List<Square> solSquares = m_GameData.getBoard().getSolution().getSquare();
            for (Square currSquare : solSquares)
            {
                int currR, currC;
                currR = currSquare.getRow().intValue();
                currC = currSquare.getColumn().intValue();
                if (currR <= 0 || currR > rows || currC <= 0 || currC > cols) {
                    m_ExceptionOcurred.setErrorMessage("Column/Row value is not correct");
                    rightData = false;
                }
            }
        }catch (Exception e){
            m_ExceptionOcurred.setErrorMessage("couldn't convert BigInteger values");
            rightData = false;
        }
        return rightData;
    }

    private boolean checkDefinitionFromXML()
    {
        int rows = m_GameData.getBoard().getDefinition().getRows().intValue();
        int cols = m_GameData.getBoard().getDefinition().getColumns().intValue();
        return (rows >= MIN_SIZE_BOARD && rows <= MAX_SIZE_BOARD && cols >= MIN_SIZE_BOARD && cols <= MAX_SIZE_BOARD && checkSlicesFromXML(rows,cols));
    }

    private boolean checkSlicesFromXML(int i_Row, int i_Col)
    {
        List<Slice> allSlices = m_GameData.getBoard().getDefinition().getSlices().getSlice();
        int countRow = 0, countCol = 0;
        boolean rightData = false;
        boolean[] idRows, idCols;
        idRows = new boolean[i_Row + 1];
        idCols = new boolean[i_Col + 1];
        try {
            if (allSlices.size() == (i_Row + i_Col))
            {
                for (Slice currSlice : allSlices)
                {
                    String orientation = currSlice.getOrientation();
                    if (orientation.equals("row"))
                    {
                        if (checkValueOfSlices(currSlice,i_Col)) {
                            countRow += idCorrectFromXML(1, i_Row, idRows, currSlice.getId().intValue());
                        }
                    } else if (orientation.equals("column"))
                    {
                        if (checkValueOfSlices(currSlice,i_Row)) {
                            countCol += idCorrectFromXML(1, i_Col, idCols, currSlice.getId().intValue());
                        }
                    } else
                    {
                        m_ExceptionOcurred.setErrorMessage("Orientation of slice is not correct");
                    }
                }
            }
            else
            {
                m_ExceptionOcurred.setErrorMessage("The amount of slices is not correct");
            }
            if (allSlices.size() == (countCol + countRow)) {
                rightData = true;
            }
            else
            {
                m_ExceptionOcurred.setErrorMessage("The id of the slice is not correct");
            }
        }
        catch (Exception e) {
            rightData = false;

        }
        finally {
            return rightData;
        }
    }

    private boolean checkValueOfSlices(Slice i_Slice,int i_CompParam)
    {
        boolean retValue = false;
        LinkedList<Integer> slices = new LinkedList<>();
        int sum = 0;
        String[] sizeB = i_Slice.getBlocks().trim().split(",");
        if(sizeB.length < (i_CompParam + 1) / 2)
        {
            for (String currSlice: sizeB)
            {
                currSlice = currSlice.trim();
                slices.addLast(Integer.parseInt((currSlice)));
                sum += slices.getLast();
            }
            if(slices.size() + sum - 1 <= i_CompParam)
            {
                retValue = true;
            }else
            {
                m_ExceptionOcurred.setErrorMessage("The values on a slice is not correct");
            }
        }
        return retValue;
    }


    private int idCorrectFromXML(int i_MinValue, int i_MaxValue,boolean[] i_IdArray, int i_CurrId)
    {
        int count = 0;
        if (i_CurrId >= i_MinValue && i_CurrId <= i_MaxValue && !i_IdArray[i_CurrId])
        {
            i_IdArray[i_CurrId] = true;
            count++;
        }
        return count;
    }

    public GameDescriptor getGameData() {
        return m_GameData;
    }


}
