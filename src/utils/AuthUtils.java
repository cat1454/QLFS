package utils;

/**
 * Lớp tiện ích để quản lý phân quyền trong ứng dụng
 */
public class AuthUtils {
        private static int loggedInUserId;
        private static String loggedInRole;
    private static String loggedInUserName;
     
    /**
     * Kiểm tra xem người dùng hiện tại có quyền thực hiện hành động không
     * 
     * @param requiredRole Vai trò cần thiết để thực hiện hành động
     * @return boolean Trả về true nếu người dùng có quyền, ngược lại false
     */
     public static void login(String username,int userId,String role){
         loggedInUserName = username;
         loggedInUserId = userId;
         loggedInRole = role;
     }   
     
    public static boolean hasPermission(String requiredRole) {
        String currentRole = view.LoginView.currentUserRole;
  
        // Nếu không yêu cầu quyền gì, cho phép tất cả người dùng
        if (requiredRole == null || requiredRole.isEmpty()) {
            return true;
        }
        
        // Admin có tất cả các quyền
        if (currentRole.equalsIgnoreCase("admin")) {
            return true;
        }
      
        
        // Người dùng có vai trò khớp với vai trò yêu cầu
        if (currentRole.equalsIgnoreCase(requiredRole)) {
            return true;
        }
        
  
        // Mặc định không có quyền
        return false;
    }
      public static int getLoggedInUserId() {
        return loggedInUserId;
}
    
    /**
     * Kiểm tra xem người dùng hiện tại có phải là admin không
     * 
     * @return boolean Trả về true nếu là admin, ngược lại false
     */
    public static boolean isAdmin() {
        return view.LoginView.currentUserRole.equalsIgnoreCase("Admin");
    }
    
    
    /**
     * Kiểm tra xem người dùng hiện tại có phải là nhân viên không
     * 
     * @return boolean Trả về true nếu là nhân viên, ngược lại false
     */
   
    
    /**
     * Kiểm tra xem người dùng hiện tại có phải là người dùng thông thường không
     * 
     * @return boolean Trả về true nếu là người dùng thông thường, ngược lại false
     */
    public static boolean isUser() {
        return view.LoginView.currentUserRole.equalsIgnoreCase("user");
    }

    public static void setCurrentUserId(int userId ) {
        
            loggedInUserId = userId;
           
    }
    public static int getCurrentUserId() {
        
            return loggedInUserId ;
    }
  
    public static void setCurrentUserName(String userName ) {
        
            loggedInUserName = userName;
           
    }
    public static String getCurrentUserName() {
        return loggedInUserName;
    }
}
