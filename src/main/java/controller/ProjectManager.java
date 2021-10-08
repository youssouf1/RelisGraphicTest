package controller;

import lombok.NonNull;
import model.RelisUser;
import model.Screening;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.*;
import view.ScreeningView;

import java.io.File;

import java.util.ArrayList;
import java.util.List;


public class ProjectManager {




    // FUNCTIONS PROGRAMMING

    // check if a paper is present given the list of paper
    private static final ThirdParamsFunctions paperIsPresent =
            ProjectManager::checkPaperPresence;
    // delete a paper
    private static final ThirdParamsFunctions deletePaper_func
            = ProjectManager::deletePaper;
    private static  final FourthParamsFunctions hasARole
            = ProjectManager::HasARole;



    public void createProject(WebDriver driver, String fileName) {

        // open the file and check if exist
        File file = FileUtils.openFile(fileName);
        // return if the file doesn't exist
        if (file == null) return;
        // push the  create new project button
        driver.findElement(By.linkText(ProjectUtils.LK_ADD_NEW_PROJECT_BUTTON)).click();
        // go to upload mode
        driver.findElement(By.linkText(ProjectUtils.LINK_TEST_UPLOAD_MODE)).click();

        // upload the '.php' file for the new project
        driver.findElement(By.name(ProjectUtils.NAME_CHOOSE_FILE_ELEMENT)).sendKeys(file.getAbsolutePath());
        Utility.sleep(4);
        // then create the project by clicking the submit button
        driver.findElement(By.cssSelector("button[type='submit']")).click();

    }


    public void openProject(WebDriver driver, String projectName) {
        //TODO a completer !!!
        driver.findElement(By.cssSelector(ProjectUtils.CLASS_OPEN_PROJECT)).click();

    }

    /**
     *  delete all the papers for the current open project
     * @param driver
     */
    public void deleteAllPapers(WebDriver driver){

        openAllPaper(driver);
        // click the delete all button
        driver.findElement(By.linkText(ProjectUtils.LK_DELETE_ALL_PAPERS_BUTTON)).click();
        // confirm the deletion
        driver.findElement(By.linkText(ProjectUtils.LK_CONFIRM_DELETE_ALL_PAPERS)).click();
    }

    /**
     * @param driver
     * @param fileName the file where the bibtex is located
     *                 This procedure implement the function for uploading a paper
     *                 using Bibtex
     */
    public void uploadFromBibTeXPaper(WebDriver driver, String fileName) {


        String bib_msg = FileUtils.getLinesFrom(fileName);

        // open the all paper menu
        openAllPaper(driver);
        // push to bibtex button
        driver.findElement(By.linkText(PaperUtils.BIBTEX_MODE)).click();
        driver.findElement(By.name(PaperUtils.NAME_BIBTEX_TEXT_AREA)).
                sendKeys(bib_msg);
        driver.findElement(By.cssSelector(PaperUtils.CSS_SUBMIT_BUTTON)).click();
        // close the bibtex mode
        driver.findElement(By.className(PaperUtils.CLASS_CLOSE_PAPER_BUTTON)).click();

    }


    private static void openAllPaper(WebDriver driver) {

        // go to the paper
        driver.findElement(By.className(PaperUtils.CLASS_NAME_OPEN_PAPER)).click();
        // go to all paper
        driver.findElement(By.linkText(PaperUtils.LINK_TEXT_ALL_PAPER)).click();

    }


    /**
     * importing papers from bibtex file
     *
     * @param driver
     * @param fileName
     */
    public void importBibTexPapers(WebDriver driver, String fileName) {


        // push the import menu
        driver.findElement(By.linkText(PaperUtils.LK_IMPORT_PAPER)).click();
        //choose using Bibtex option
        new WebDriverWait(driver, 5).until(ExpectedConditions.elementToBeClickable(
                By.linkText(PaperUtils.LK_BIBTEX_IMPORT_MODE))).click();
        File bib_file = new File(fileName);
        // we gotta choose the file and import all the papers from it
        driver.findElement(By.name(PaperUtils.NAME_BIBTEX_FILE_CHOOSE_ELEM)).sendKeys(bib_file.getAbsolutePath());
        driver.findElement(By.className(PaperUtils.CLASS_UPLOAD_IMPORTED_PAPERS_BUTTON)).click();

        // now we  commit the imported papers
        driver.findElement(By.className(PaperUtils.CLASS_UPLOAD_IMPORTED_PAPERS_BUTTON)).click();
        // back to the all papers
        //driver.findElement(By.className(utils.PaperUtils.CLASS_BACK_FROM_PAPERS_IMPORT_BUTTON)).click();

    }

    /**
     * adding a reviewer to a project
     *
     * @param driver
     * @param user
     */
    private void addRoleForProject(WebDriver driver, RelisUser user,String role) {

        // go to the users page
        driver.findElement(By.className(ProjectUtils.CLASS_PROJECT_USERS)).click();

        // push the + button
        driver.findElement(By.className(ProjectUtils.CLASS_ADD_USER_BUTTON)).click();
        // now we show all the users
        driver.findElement(By.className(ProjectUtils.CLASS_SHOW_ALL_USERS)).click();
        WebElement users_ul = driver.findElement(By.id(ProjectUtils.ID_RELIS_ALL_USERS));
        // we have the list that contains all the users
        List<WebElement> links = users_ul.findElements(By.tagName("li"));
        // choose the user to assign as a reviewer
        WebElement webElement = Utility.chooseWebElement(links, user.getFull_name());
        if(webElement != null) webElement.click();
        // show the  all users role
        driver.findElement(By.id(ProjectUtils.ID_USER_ROLES_OPTIONS)).click();
       // we gotta choose a specific role
        WebElement list_of_roles = driver.findElement(By.id(ProjectUtils.ID_ALL_USER_ROLE_FOR_A_PROJECT));
        List<WebElement> roles = list_of_roles.findElements(By.tagName("li"));
        // we choose the user role and click it
        roles.stream()
                .filter(p -> role.equals(p.getText()))
                .findFirst()
                .ifPresent(WebElement::click);

        driver.findElement(By.className(ProjectUtils.CLASS_SUCCESS_BUTTON)).click();

    }

    /**
     * add a user as a reviewer for the current project
     * @param driver
     * @param user
     */
    public void addReviewer(WebDriver driver, RelisUser user){


        this.addRoleForProject(driver,user,ProjectUtils.REVIEWER_ROLE);
    }

    /**
     * assign a user as a project manager
     * @param driver
     * @param user
     */
    public void addProjectManager( WebDriver driver, RelisUser user){
        this.addRoleForProject(driver,user,ProjectUtils.PROJECT_MANAGER_ROLE);
    }

    /**
     * adding a user as a validator for the current project
     * @param driver
     * @param user
     */
    public void addValidator(WebDriver driver, RelisUser user){
        this.addRoleForProject(driver,user,ProjectUtils.VALIDATOR_ROLE);
    }

    /**
     *  add a user as a guest for the current open project
     * @param driver
     * @param user
     */
    public void addUserAsGuest(WebDriver driver, RelisUser user){
        this.addRoleForProject(driver,user,ProjectUtils.GUEST_ROLE);
    }


    /**
     * remove a reviewer from a project
     *
     * @param driver
     * @param reviewer
     */
    public void removeUserRole(WebDriver driver, RelisUser reviewer) {
        // go to the users page
        driver.findElement(By.className(ProjectUtils.CLASS_PROJECT_USERS)).click();

        WebElement table = driver.findElement(By.id(ProjectUtils.ID_PROJECT_TABLE_USERS));
        List<WebElement> users = table.findElements(By.tagName("tr"));
        WebElement user_manager = getUserWebElement(users, reviewer.getFull_name());
        if (user_manager != null){
            user_manager.findElement(By.className(ProjectUtils.CLASS_REMOVE_PROJECT_USER)).click();
            Alert alert = driver.switchTo().alert();
            alert.accept();
        }

    }


    /**
     *  get all the papers from a project
     * @param driver
     * @return
     */
    private static int work_through_paper(WebDriver driver, Functions work,
                                          String COUNT_MODE, String subject){
        openAllPaper(driver);
        return work_through_table(driver, work,COUNT_MODE, subject, "");
    }

    /**
     *
     * get the length of the papers for the current project
     * @param driver
     * @return
     */
    public  int getProjectPapersLength(WebDriver driver){

        return work_through_paper(driver,null, PaperUtils.COUNT_PAPER_MODE,"");
    }

    /**
     * delete a specific paper using his key
     * @param driver web driver
     * @param papers the list of the papers
     * @return  1 if the paper is deleted or not existing
     */
    private static int deletePaper(WebDriver driver, List<WebElement> papers,String paper){

        // get the web element for the paper to delete
        WebElement user_manager = getUserWebElement(papers, paper);
        if (user_manager != null){ // do we found the paper?
            // if so , we delete it
            user_manager.findElement(By.className(ProjectUtils.CLASS_REMOVE_PROJECT_USER)).click();
            Alert alert = driver.switchTo().alert();
            alert.accept();
            return 1;
        }
        return 0;

    }

    /**
     *  delete a given paper identified by his key
     *
     * @param driver the web driver
     * @param key the id of the paper
     *
     */
    public  void deletePaperByKey(WebDriver driver, String key){

        work_through_paper(driver,deletePaper_func,"",key);
    }

    /**
     * check if a specific paper exist in a project
     * @param driver the current web driver
     * @param key the paper identified by the key
     * @return true if the paper exist otherwise false
     */
    public boolean isPresentPaper(WebDriver driver, String key){
        int papers = work_through_paper(driver,paperIsPresent,"",key);

        return papers == 1;
    }


    private static int checkPaperPresence(WebDriver driver, List<WebElement> papers,String paper){

        // get the web element for the paper to delete
        WebElement user_manager = getUserWebElement(papers, paper);
        return (user_manager == null) ? 0:1;
    }





    /**
     * get the specific user from a project users
     * @param users_data the list of the users
     * @param user_name the user that we're looking for
     * @return the web element for the user passed if exists otherwise null
     */
    private static WebElement getUserWebElement( List<WebElement> users_data, String user_name){


        for(int i=0; i< users_data.size(); i++){
            WebElement user = users_data.get(i);
            List<WebElement> user_info= user.findElements(By.tagName("td"));

            for (int j = 0; j < user_info.size(); j++)
                if(user_info.get(1).getText().equals(user_name))
                    return user;
        }
        return null;
    }


    /***
     *
     * @param users the list of users
     * @param user_name the username that we are looking for
     * @param role the user role
     * @return the user web element if exist otherwise null
     */
    private static WebElement checkUserRoleWebElement( List<WebElement> users, String user_name, String role){


        for(int i=0; i< users.size(); i++){

            WebElement user = users.get(i);
            List<WebElement> user_info= user.findElements(By.tagName("td"));
            String current_user_name = user_info.get(1).getText();
            String current_user_role = user_info.get(2).getText();
            if(current_user_name.equals(user_name) && current_user_role.equals(role))
               return user;
        }
        return null;

    }

    /**
     *
     * @param users the list of the users
     * @param user the current user we're looking for
     * @param role the user role
     * @return true if the user exist and has the role(param role) otherwise false
     */
    private static int HasARole( WebDriver driver,List<WebElement> users, String user,String role){

       return   (checkUserRoleWebElement(users,user,role) != null)? 1: 0;

    }







    /**
     *
     * @param driver the wen driver
     * @param user the user
     * @return true if the user has a reviewer role
     */
    public static boolean isAReviewer(WebDriver driver,@NonNull RelisUser user){

        int x = work_through_userRoles(driver,hasARole,
                user.getFull_name()
        ,ProjectUtils.REVIEWER_ROLE);
        return x ==1;
    }

    /**
     *
     * @param driver the wen driver
     * @param user the user
     * @return true if the user has a project manager role
     */
    public static boolean isAProjectManager(WebDriver driver, @NonNull RelisUser  user){
        int x = work_through_userRoles(driver,hasARole,
                user.getFull_name()
                ,ProjectUtils.PROJECT_MANAGER_ROLE);
        return x ==1;

    }

    /**
     *
     * @param driver the wen driver
     * @param user the user
     * @return true if the user has a validator role
     */
    public static boolean isAValidator(WebDriver driver, @NonNull RelisUser  user){
        int x = work_through_userRoles(driver,hasARole,
                user.getFull_name()
                ,ProjectUtils.VALIDATOR_ROLE);
        return x ==1;
    }
    /**
     *
     * @param driver the wen driver
     * @param user the user
     * @return true if the user has a guest role
     */
    public static boolean isAGuestUser(WebDriver driver, @NonNull RelisUser  user){
        int x = work_through_userRoles(driver,hasARole,
                user.getFull_name()
                ,ProjectUtils.GUEST_ROLE);
        return x ==1;

    }


    private static int work_through_userRoles(WebDriver driver, Functions action,
                                              String subject, String role){

        // go to the users page
        driver.findElement(By.className(ProjectUtils.CLASS_PROJECT_USERS)).click();
        return work_through_table(driver,action, "",subject,role);
    }
    /***
     *
     * @param driver the web driver
     * @param action a function
     * @param COUNT_MODE do we count the items of the table??
     * @param subject the item we're looking for
     * @param role the user role
     * @return 1 if the everything went normally otherwise 0
     */
    private static int work_through_table(WebDriver driver, Functions action, String COUNT_MODE,
                                          String subject, String role){

        try {
            // select the table that contains the papers
            WebElement table = driver.findElement(By.id(ProjectUtils.ID_PROJECT_TABLE_USERS));

            WebElement element ;
            int result =0;

            while (true){
                try{
                    // get web element for the next click link
                    element = driver.findElement(By.id(ProjectUtils.ID_NEXT_PAPERS_PAGE));
                    // get all the papers present from the current table
                    List<WebElement> other_papers = table.findElements(By.tagName("tr"));
                    // we remove the first web element which is the table header
                    other_papers.remove(0);
                    result = (COUNT_MODE.equals(PaperUtils.COUNT_PAPER_MODE))?
                            (result+other_papers.size()):
                            (!role.equals("")) ?((FourthParamsFunctions) action).apply(driver,other_papers,subject,role)
                                    :((ThirdParamsFunctions) action).apply(driver,other_papers,subject);
                    // do we did the change that we want??
                    if(!COUNT_MODE.equals(PaperUtils.COUNT_PAPER_MODE) && result == 1)
                        return result; // so we return

                    // there is no next table  ?
                    if(Utility.hasClass(element,"disabled")) break;
                    element.findElement(By.linkText("Next")).click();
                } catch (Exception e){
                    System.out.println("ERROR " + e.getMessage());
                    return 0;
                }
            }
            return  result;

        } catch (Exception e){
            return  0;
        }


    }




    public ArrayList<RelisUser> assginReviewerForScreening(WebDriver driver, RelisUser connectUser){

        if(!connectUser.getUser_usergroup().equals("1")) return null;
        // we go to the project phases
        driver.findElement(By.className(ProjectUtils.CLASS_HOME_PROJECT)).click();
        return ScreeningView.assign_papers(driver);

    }

    public void getAllPapers(WebDriver driver){
        openAllPaper(driver);
        Utility.getAllPapers(driver);
    }

    public void startScreeningPhase(WebDriver driver){


//        ArrayList<RelisUser> reviewers = Screening.getReviewer(driver);
//
//        RelisUser relisUser = reviewers.get(0);
//        Screening screening = new Screening(driver,relisUser);



    }





}