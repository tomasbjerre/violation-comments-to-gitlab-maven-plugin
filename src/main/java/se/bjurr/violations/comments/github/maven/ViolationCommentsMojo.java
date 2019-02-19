package se.bjurr.violations.comments.github.maven;

import static org.apache.maven.plugins.annotations.LifecyclePhase.NONE;
import static se.bjurr.violations.comments.gitlab.lib.ViolationCommentsToGitLabApi.violationCommentsToGitLabApi;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;

import java.util.ArrayList;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.gitlab4j.api.Constants.TokenType;
import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.util.Filtering;

@Mojo(name = "violation-comments", defaultPhase = NONE)
public class ViolationCommentsMojo extends AbstractMojo {

  @Parameter(property = "violations", required = false)
  private List<ViolationConfig> violations;

  @Parameter(property = "commentOnlyChangedContent", required = false, defaultValue = "true")
  private boolean commentOnlyChangedContent;

  @Parameter(
      property = "createCommentWithAllSingleFileComments",
      required = false,
      defaultValue = "true")
  private boolean createCommentWithAllSingleFileComments;

  @Parameter(property = "createSingleFileComments", required = false, defaultValue = "true")
  private boolean createSingleFileComments;

  @Parameter(property = "gitLabUrl", required = false)
  private String gitLabUrl;

  @Parameter(property = "apiToken", required = false)
  private String apiToken;

  @Parameter(property = "projectId", required = false)
  private String projectId;

  @Parameter(property = "mergeRequestIid", required = false)
  private String mergeRequestIid;

  @Parameter(property = "ignoreCertificateErrors", required = false, defaultValue = "true")
  private Boolean ignoreCertificateErrors;

  @Parameter(property = "apiTokenPrivate", required = false, defaultValue = "true")
  private Boolean apiTokenPrivate;

  @Parameter(property = "minSeverity", required = false, defaultValue = "INFO")
  private SEVERITY minSeverity;

  @Parameter(property = "keepOldComments", required = false)
  private Boolean keepOldComments;

  @Parameter(property = "shouldSetWip", required = false)
  private Boolean shouldSetWip;

  @Parameter(property = "commentTemplate", required = false)
  private String commentTemplate;

  @Parameter(property = "proxyServer", required = false)
  private String proxyServer;

  @Parameter(property = "proxyUser", required = false)
  private String proxyUser;

  @Parameter(property = "proxyPassword", required = false)
  private String proxyPassword;

  @Override
  public void execute() throws MojoExecutionException {
    if (mergeRequestIid == null || mergeRequestIid.isEmpty()) {
      getLog().info("No merge request id defined, will not send violation comments to GitLab.");
      return;
    }

    if (violations == null || violations.isEmpty()) {
      getLog().info("No violations configured.");
      return;
    }

    getLog()
        .info(
            "Will comment project "
                + projectId
                + " and MR "
                + mergeRequestIid
                + " on "
                + gitLabUrl);

    List<Violation> allParsedViolations = new ArrayList<>();
    for (final ViolationConfig configuredViolation : violations) {

      final List<Violation> parsedViolations =
          violationsApi() //
              .findAll(configuredViolation.getParser()) //
              .inFolder(configuredViolation.getFolder()) //
              .withPattern(configuredViolation.getPattern()) //
              .withReporter(configuredViolation.getReporter()) //
              .violations();
      if (minSeverity != null) {
        allParsedViolations = Filtering.withAtLEastSeverity(allParsedViolations, minSeverity);
      }
      allParsedViolations.addAll(parsedViolations);
    }

    try {
      final TokenType tokenType = apiTokenPrivate ? TokenType.PRIVATE : TokenType.ACCESS;
      final Integer mergeRequestIidInteger = Integer.parseInt(mergeRequestIid);
      violationCommentsToGitLabApi() //
          .setHostUrl(gitLabUrl) //
          .setProjectId(projectId) //
          .setMergeRequestIid(mergeRequestIidInteger) //
          .setApiToken(apiToken) //
          .setTokenType(tokenType) //
          .setCommentOnlyChangedContent(commentOnlyChangedContent) //
          .setCreateCommentWithAllSingleFileComments(createCommentWithAllSingleFileComments) //
          .setCreateSingleFileComments(createSingleFileComments) //
          .setIgnoreCertificateErrors(ignoreCertificateErrors) //
          .setViolations(allParsedViolations) //
          .setShouldKeepOldComments(keepOldComments) //
          .setShouldSetWIP(shouldSetWip) //
          .setCommentTemplate(commentTemplate) //
          .setProxyServer(proxyServer) //
          .setProxyUser(proxyUser) //
          .setProxyPassword(proxyPassword) //
          .toPullRequest();
    } catch (final Exception e) {
      getLog().error(e.getMessage(), e);
    }
  }
}
