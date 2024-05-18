package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;


/*
 * This class represents the service or the business layer of the Projects application.
 * 
 */
public class ProjectService {
  private ProjectDao projectDao = new ProjectDao();

  /*
   * This methods called the DAO class to insert a project row.
   */
  public Project addProject(Project project) {
    return projectDao.insertProject(project);
  }

  /*
   * This method calls the project DAO to gather all project rows without the details and returns a
   * list of project records. 
   * Stream method is used to sort all projects by project ID.
   */
  public List<Project> fetchAllProjects() {
    // formatter:off
    return projectDao.fetchAllProjects().stream()
        .sorted((r1, r2) -> r1.getProjectId() - r2.getProjectId()).collect(Collectors.toList());
    // formatter:on
  }

  /*
   * This method calls the project DAO to get all project details using the project ID. If the
   * project ID is invalid, it will throw an exception.
   */
  public Project fetchProjectById(Integer projectId) {
    return projectDao.fetchProjectbyId(projectId).orElseThrow(() -> new NoSuchElementException(
        "Project with project ID=" + projectId + " does not exist."));
  }

  /*
   * This method calls the project DAO and passes the project object as a parameter. Returns boolean
   * if the UPDATE operation is successful. if the project ID is invalid, it will throw an exception.
   */
  public void modifyProjectDetails(Project project) {
    if (!projectDao.modifyProjectDetails(project)) {
      throw new DbException("Project with ID =" + project.getProjectId() + " does not exist.");
    }
  }

  /*
   * This method calls the project DAO to delete the project with the provided project ID. If the
   * project ID is invalid, it will throw an exception.
   */
  public void deleteProject(Integer projectId) {
    if (!projectDao.deleteProject(projectId)) {
      throw new DbException("Project with ID =" + projectId + " does not exist.");
    }

  }
}
