package GriddlerWeb.GameLogic;

import javafx.util.Pair;

import java.util.ArrayList;

/**
 * Created by nicom on 10/25/2016.
 */
public class PerfectBlock implements IPerfectBlock {

    private int m_rows,m_cols;
    private BoardInfo.BoardOptions[][] m_Board;
    private ArrayList<BlockValues>[] m_ColsBloks,m_RowBlocks;
    private ArrayList<Pair<Integer,Integer>>[] m_PossibleColBlockPlace,m_PossibleRowBlockPlace; // keeps the posible start cell and the end cell of every block

    public PerfectBlock(int m_rows, int m_cols, BoardInfo.BoardOptions[][] m_Board, ArrayList<BlockValues>[] i_ColsBloks, ArrayList<BlockValues>[] i_RowBlocks) {
        this.m_rows = m_rows;
        this.m_cols = m_cols;
        this.m_Board = m_Board;
        this.m_ColsBloks = i_ColsBloks;
        this.m_RowBlocks = i_RowBlocks;
        m_PossibleColBlockPlace = new ArrayList[i_ColsBloks.length];
        m_PossibleRowBlockPlace = new ArrayList[i_RowBlocks.length];
        initializeRowColPossiblePlaceBlock();
    }

    private void initializeRowColPossiblePlaceBlock() {
        initializePossibleBlock(m_ColsBloks,m_PossibleColBlockPlace,m_rows);
        initializePossibleBlock(m_RowBlocks,m_PossibleRowBlockPlace,m_cols);
    }

    private void initializePossibleBlock(ArrayList<BlockValues>[] i_Blocks, ArrayList<Pair<Integer,Integer>>[] i_PossiPosition,int i_Length)
    {
        final int start=0, end = i_Length;

        for(int i =0 ; i< i_Blocks.length ; i++)
        {
            i_PossiPosition[i] = new ArrayList<>();
            int sumOfBlocks=0 , sumUntilNow =0;
            for(int currBlock =0 ; currBlock< i_Blocks[i].size() ; currBlock++)
            {
                sumOfBlocks+= i_Blocks[i].get(currBlock).getValue();
            }
            for(int currBlock =0 ; currBlock< i_Blocks[i].size() ; currBlock++)
            {
                Integer specificStart ,specificEnd;
                int amountOfSpaces = currBlock;
                specificStart = start + amountOfSpaces + sumUntilNow;
                sumUntilNow +=i_Blocks[i].get(currBlock).getValue();
                specificEnd = (end- 1) - (sumOfBlocks - sumUntilNow) - (i_Blocks[i].size()- (currBlock + 1));
                i_PossiPosition[i].add(new Pair<>(specificStart,specificEnd));
            }
        }
    }
    @Override
    public void updatePerectScore() {
        updateSpecificPerfectBlocks(m_rows,m_cols,m_RowBlocks,m_PossibleRowBlockPlace,false);
        updateSpecificPerfectBlocks(m_cols,m_rows,m_ColsBloks,m_PossibleColBlockPlace,true);
        System.out.println("finish");
    }

    private void updateSpecificPerfectBlocks (int rows,int cols ,ArrayList<BlockValues>[] i_Blocks,ArrayList<Pair<Integer,Integer>>[] i_PossibleBlockPlace,boolean i_IsCol){
        ArrayList<SequenceData>[] sequences = new ArrayList[i_Blocks.length];
        resetBoolValueOnBlock(i_Blocks);
        getSequencesFromBoard(sequences,rows,cols,i_IsCol);
        checkSequenceFromBoard(sequences,i_Blocks,i_PossibleBlockPlace);

    }

    private void checkSequenceFromBoard(ArrayList<SequenceData>[] i_Sequences, ArrayList<BlockValues>[] i_Blocks, ArrayList<Pair<Integer, Integer>>[] i_PossibleBlockPlace) {
        int realBlockSize;
        boolean isPerfect = false;
        for (int i = 0; i < i_Sequences.length; i++) {
            realBlockSize = i_Blocks[i].size() - 1;
            if (i_Sequences[i].size() > 0 && i_Sequences[i].size() <= i_Blocks[i].size()) { // checked that the amount of seq are less or equal to the blocks
                int currIndxFound = 0;
                for (SequenceData currSeq : i_Sequences[i]) // check for each seq in sequence[i] if it exist in the block array,if there is one that not exist the row or col is not correct
                {// we onli take the sequences that appear in right order, if there is block 1,2,3  and seq  XXX-XX-X  it will not work
                    boolean foundSeq = false;
                    while (!foundSeq && currIndxFound <= realBlockSize) {
                        if (currSeq.getAmount().equals(i_Blocks[i].get(currIndxFound).getValue()))
                        {
                            if(currIndxFound == 0 && currSeq.getSeqStart() >= i_PossibleBlockPlace[i].get(currIndxFound).getKey()
                                    &&  currSeq.getSeqEnd() < i_PossibleBlockPlace[i].get(currIndxFound).getKey() + currSeq.getAmount() +1)
                            {
                                isPerfect= true;
                            }
                            else if(currIndxFound == i_Blocks[i].size() - 1 && currSeq.getSeqEnd() <= i_PossibleBlockPlace[i].get(currIndxFound).getValue()
                                    && currSeq.getSeqStart() >= i_PossibleBlockPlace[i].get(currIndxFound).getValue() - 1 -currSeq.getAmount())
                            {
                                isPerfect = true;
                            }
                            else if(i_Sequences[i].size() < i_Blocks[i].size() && ((currIndxFound  < realBlockSize  && i_Blocks[i].get(currIndxFound).getValue() == i_Blocks[i].get(currIndxFound + 1).getValue())||
                                    (currIndxFound >0 && i_Blocks[i].get(currIndxFound).getValue() == i_Blocks[i].get(currIndxFound - 1).getValue())))
                            {
                                foundSeq = true;
                            }
                            else if (currSeq.getSeqStart() >= i_PossibleBlockPlace[i].get(currIndxFound).getKey() &&
                                    currSeq.getSeqEnd() <= i_PossibleBlockPlace[i].get(currIndxFound).getValue()) {// check the start point and end point concur with the possible place of the block

                                isPerfect = true;
                            }

                            if(isPerfect){
                                foundSeq = true;
                                i_Blocks[i].get(currIndxFound).setBlockPerfect();
                                isPerfect =false;
                            }
                        }
                        currIndxFound++;
                    }
                }
            }
        }
    }



    private boolean checkPerfectWhenSameNumber(SequenceData i_CurrSeq,ArrayList<SequenceData> i_Sequence, ArrayList<BlockValues> i_Block, ArrayList<Pair<Integer,Integer>> i_PossibleBlock, int i_IndexSeq) {
        int startSeq = i_CurrSeq.getSeqStart(), endSeq = i_CurrSeq.getSeqEnd();
        int nextBlockPossibleStart = i_PossibleBlock.get(i_IndexSeq + 1).getKey(), nextBlockPossibleEnd = i_PossibleBlock.get(i_IndexSeq + 1).getValue();
        boolean retVal = true;
        if (i_Sequence.size() > i_IndexSeq + 1) {
            SequenceData nextSeq = i_Sequence.get(i_IndexSeq +1);
            if (nextSeq.getAmount() == i_CurrSeq.getAmount() && i_Block.get(i_IndexSeq).getValue() == i_Block.get(i_IndexSeq + 1).getValue()) {
                if (startSeq >= nextBlockPossibleStart && nextBlockPossibleEnd >= endSeq) {
                    retVal = false;
                }
            } else {
                retVal = false;
            }
        }
        else{
            retVal =false;
        }
        return retVal;
    }



    private void getSequencesFromBoard(ArrayList<SequenceData>[] i_Sequences ,int i_Row,int i_Cols , boolean i_IsCol) // check first and last
    {
        for(int rIndex = 0 ; rIndex < i_Row ; rIndex ++)
        {
            boolean foundStarterWhite = true;
            int sequenceLen =0;
            i_Sequences[rIndex] = new ArrayList<>();
            for ( int cIndex = 0 ; cIndex < i_Cols ; cIndex++)
            {
                BoardInfo.BoardOptions currOption = (i_IsCol) ? m_Board[cIndex][rIndex] : m_Board[rIndex][cIndex];
                if (currOption.equals(BoardInfo.BoardOptions.UNDEFINED)){
                    foundStarterWhite= false;
                    sequenceLen =0 ;
                }
                else if(currOption.equals(BoardInfo.BoardOptions.EMPTY) && !foundStarterWhite){
                    foundStarterWhite = true;
                }
                else if(foundStarterWhite && currOption.equals(BoardInfo.BoardOptions.FILLED)){
                    sequenceLen++;
                    if(cIndex +1 == i_Cols)
                    {
                        int newIndex = cIndex +1;
                        i_Sequences[rIndex].add(new SequenceData(newIndex - sequenceLen,newIndex -1 , sequenceLen));
                    }
                }
                else if(currOption.equals(BoardInfo.BoardOptions.EMPTY) && sequenceLen>0 && foundStarterWhite)
                {
                    i_Sequences[rIndex].add(new SequenceData(cIndex - sequenceLen,cIndex -1,sequenceLen));
                    sequenceLen = 0;
                }
            }
        }
    }

    private void resetBoolValueOnBlock(ArrayList<BlockValues>[] i_Blocks)
    {
        for(int i =0 ; i < i_Blocks.length ; i++)
        {
            for(BlockValues currBlock : i_Blocks[i])
            {
                currBlock.setBlockUnperfect();
            }
        }
    }

    private class SequenceData
    {
        Integer m_SeqStart;
        Integer m_SeqEnd;
        Integer m_Amount;
        Integer m_SeqLen;

        public SequenceData(Integer i_SeqStart, Integer i_SeqEnd, Integer i_Amount) {
            this.m_SeqStart = i_SeqStart;
            this.m_SeqEnd = i_SeqEnd;
            this.m_Amount = i_Amount;
            this.m_SeqLen = i_Amount -1;
        }

        public Integer getSeqStart() {
            return m_SeqStart;
        }

        public Integer getSeqEnd() {
            return m_SeqEnd;
        }

        public Integer getAmount() {
            return m_Amount;
        }

        public Integer getSeqLen() {
            return m_SeqLen;
        }
    }
}
