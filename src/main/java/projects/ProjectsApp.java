package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

/*
 * This class contains the main method for the ProjectsApp Application. This class is a menu drive
 * application that accepts user input from the console. It performs CRUD operation on the project
 * table.
 * 
 */

public class ProjectsApp {

  private Scanner scanner = new Scanner(System.in);
  private ProjectService projectService = new ProjectService();
  private Project curProject;


  /*
   * This is the list of available operations that can be performed.
   */

  //@formatter:off
  private List<String> operations = List.of(
      "1) Add a project",
      "2) List of projects",
      "3) Select a project",
      "4) Update a project",
      "5) Delete a project"
    );
  //@formatter:off
  
  // The java application entry point. 
  
  public static void main(String[] args) {
   new ProjectsApp().processUserSelections();
     }

/*
 * This methods prints the available operation options,
 * gets user selection and performs the selected operation. 
 */
  private void processUserSelections() {
    boolean done = false;
    
    while(!done) {
      try {
        int selection = getUserSelection();
        switch (selection) {
          case -1:
          done = exitMenu();
          break;
          
          case 1:
            createProject();
            break;
          
          case 2:
            listProjects();
            break;
           
          case 3:
            selectProject();
            break;
            
          case 4:
            updateProjectDetails();
            break;
            
          case 5: 
            deleteProject();
            break;
          
        default:
          System.out.println("\n" + selection + " is not a valid selection. Try again.");
        }
      }
        catch(Exception e) {
          System.out.println("\nError: " + e + " Try again.");
      }
    }
  }
  
  /*
   * This method allows us to delete the selected project. It displays a list of projects,
   * allows user to select the project ID to be deleted, then calls project service to 
   * delete the project associated with the entered project ID.
   * 
  */
private void deleteProject() {
  listProjects();
  
  Integer projectId = getIntInput("Enter the ID of the project to delete");
  
 
  
  projectService.deleteProject(projectId);
  System.out.println("Project" + projectId + " was deleted successfully.");
  
  if(Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
    curProject = null;
  }
}

/*
 * This methods allows us to update the selected project's details.
 * It prompts the user to enter the project ID they desire to update. 
 */
private void updateProjectDetails() {
  if(Objects.isNull(curProject)) {
    System.out.println("\nPlease select a project.");
    return;
  }
  
  String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");
  BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours [" + curProject.getEstimatedHours() + "]");
  BigDecimal actualHours = getDecimalInput("Enter the actual hours [" + curProject.getActualHours() + "]");
  Integer difficulty = getIntInput("Enter the project difficulty (1-5) [" + curProject.getDifficulty() + "]");
  String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");
  
  Project project = new Project();
  
  project.setProjectId(curProject.getProjectId());
  project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);
  project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);
  project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
  project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty );
  project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);
  
  projectService.modifyProjectDetails(project);
  
  curProject = projectService.fetchProjectById(curProject.getProjectId());
  
}

/*
 * This methods sets and displays the current project. It prints all available
 * project and associated project ID to the console and allows user to select the
 * project ID. If the user selects valid project ID, the working project is set. 
 */
  
private void selectProject() {
  listProjects();
  Integer projectId = getIntInput("Enter a project ID to select a project");
  
  curProject = null;
  
  curProject = projectService.fetchProjectById(projectId);
  
}

/*
 * This method provides list of all projects, prints the project ID and the names. 
 */

private void listProjects() {
  List<Project> projects = projectService.fetchAllProjects();
  
  System.out.println("\nProjects:");
  
  projects.forEach(project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
  
}

/*
 * The method below collects user input to create a project and then call the project service to create the row. 
 */
  private void createProject() {
    String projectName = getStringInput("Enter the project name");
    BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
    BigDecimal actualHours = getDecimalInput("Enter the actual hours");
    Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
    String notes = getStringInput("Enter the project notes");
    
    Project project = new Project();
    
    project.setProjectName(projectName);
    project.setEstimatedHours(estimatedHours);
    project.setActualHours(actualHours);
    project.setDifficulty(difficulty);
    project.setNotes(notes);
    
    Project dbProject = projectService.addProject(project);
    System.out.println("You have successfully created project:" + dbProject);
    
  }

  /*
   * This method gets user input from the console and converts it to BigDecimal. 
   */
  private BigDecimal getDecimalInput(String prompt) {
    
    String input = getStringInput(prompt);
    
    if(Objects.isNull(input)) {
      return null;
    }
    
    try {
      return new BigDecimal(input).setScale(2);
    } catch (NumberFormatException e) {
      throw new DbException(input + " is not a valid decimal number.");
    }
    
  }

  /*
   * This method is called when the user wants to exit the application. 
   */
  private boolean exitMenu() {
    System.out.println("\nExiting the menu.");
    return true;
  }

  /*
   * This method prints the available selection option for the user. 
   * If no selection is entered, it will cause the application to exit.
   * 
   */
  private int getUserSelection() {
    printOperations();
    
    Integer input = getIntInput("Enter a menu selection");
    
    return Objects.isNull(input) ? -1 : input;
    
  }

  /*
   * This methods prompts the user to input a selection, then converts the input to an integer value. 
   */
  private Integer getIntInput(String prompt) {
    String input = getStringInput(prompt);
    
    if(Objects.isNull(input)) {
      return null;
    }
    
    try {
      return Integer.valueOf(input);
    } catch (NumberFormatException e) {
      throw new DbException(input + " is not a valid number.");
    }
    
  }

  /*
   * This methods prompts the user for an input, if the user input is nothing, nothing is returned,
   * otherwise, the trimmed input is returned. 
   */
  private String getStringInput(String prompt) {
    System.out.println(prompt + ": ");
    String input = scanner.nextLine();
    
    return input.isBlank() ? null : input.trim();
  }

  /*
   * This methods prints all available menu operations to the console.
   */
  private void printOperations() {
    System.out.println("\nThese are the available selections, Press the Enter key to quit:");
    
    operations.forEach(line -> System.out.println(" " + line));
    
    if(Objects.isNull(curProject)) {
      System.out.println("\nYou are not working with a project.");
    }
    else {
      System.out.println("\nYou are working with a project: " + curProject);
    }
  }
}
