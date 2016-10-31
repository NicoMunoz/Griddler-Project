package GriddlerWeb.UILogic;

import java.util.*;

public class UserManager {

    private final ArrayList<User> m_UsersList;
    private final Object lockObj = new Object();

    public UserManager() {
        m_UsersList = new ArrayList<User>();
    }

    public void addUser(User newUser) {
        m_UsersList.add(newUser);
    }

    public void removeUser(String userName)
    {
        synchronized (this) {
            for (User user : m_UsersList) {
                if (userName.equals(user.GetName())) {
                    m_UsersList.remove(user);
                    break;
                }
            }
        }
    }

    public boolean isUserExists(String nameTocheck) {
            boolean result = false;
            for (User user : m_UsersList) {
                if (nameTocheck.equals(user.GetName())) {
                    result = true;
                }
            }
            return result;
    }

    public List getUsers() {
        return Collections.unmodifiableList(m_UsersList);
    }
}
