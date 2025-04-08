package carDealership;

public class SessionManager {
  private static SessionManager instance = null;
  private User currentUser;

  private SessionManager() { }

  public static synchronized SessionManager getInstance() {
      if (instance == null) {
          instance = new SessionManager();
      }
      return instance;
  }

  public void setCurrentUser(User user) {
      this.currentUser = user;
  }

  public User getCurrentUser() {
      return this.currentUser;
  }
}
