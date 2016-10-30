package GriddlerWeb.GameLogic;

/**
 * Created by nicom on 10/25/2016.
 */
public class BlockValues {
    private Integer m_Value;
    private boolean m_IsPerfect;

    public BlockValues(int i_Value) {
        this.m_Value = i_Value;
        this.m_IsPerfect = false;
    }

    public Integer getValue()
    {
        return m_Value;
    }

    public boolean isPerfect()
    {
        return m_IsPerfect;
    }

    public void setBlockPerfect()
    {
        m_IsPerfect = true;
    }

    public void setBlockUnperfect()
    {
        m_IsPerfect = false;
    }

    @Override
    public String toString() {
        return m_Value.toString();
    }

    @Override
    public int hashCode() {
        return m_Value;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Integer || obj instanceof BlockValues))
        {
            return false;
        }
        if(obj instanceof Integer) {
            Integer currObj = (Integer) obj;
            return (m_Value.equals(currObj));
        }
        else
        {
            BlockValues currObj = (BlockValues)obj;
            return (m_Value.equals(currObj.getValue()));
        }
    }
}
