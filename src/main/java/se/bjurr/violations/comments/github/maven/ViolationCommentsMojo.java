package se.bjurr.violations.comments.github.maven;

import static org.apache.maven.plugins.annotations.LifecyclePhase.NONE;
import static se.bjurr.violations.comments.gitlab.lib.ViolationCommentsToGitLabApi.violationCommentsToGitLabApi;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.gitlab4j.api.Constants.TokenType;
import se.bjurr.violations.lib.ViolationsLogger;
import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.util.Filtering;

@Mojo(name = "violation-comments", defaultPhase = NONE)
public class ViolationCommentsMojo extends AbstractMojo {

  @Parameter(property = "violations", required = false)
  private List<ViolationConfig> violations;

  @Parameter(property = "commentOnlyChangedContent", required = false, defaultValue = "true")
  private boolean commentOnlyChangedContent;

  @Parameter(property = "commentOnlyChangedFiles", required = false, defaultValue = "true")
  private boolean commentOnlyChangedFiles;

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

  @Parameter(property = "maxNumberOfComments", required = false)
  private Integer maxNumberOfComments;

  @Override
  public void execute() throws MojoExecutionException {
    if (this.mergeRequestIid == null || this.mergeRequestIid.isEmpty()) {
      this.getLog()
          .info("No merge request id defined, will not send violation comments to GitLab.");
      return;
    }

    if (this.violations == null || this.violations.isEmpty()) {
      this.getLog().info("No violations configured.");
      return;
    }

    this.getLog()
        .info(
            "Will comment project "
                + this.projectId
                + " and MR "
                + this.mergeRequestIid
                + " on "
                + this.gitLabUrl);

    final ViolationsLogger violationsLogger =
        new ViolationsLogger() {

          @Override
          public void log(final Level level, final String string) {
            if (level == Level.FINE) {
              ViolationCommentsMojo.this.getLog().debug(string);
            } else if (level == Level.SEVERE) {
              ViolationCommentsMojo.this.getLog().error(string);
            } else if (level == Level.WARNING) {
              ViolationCommentsMojo.this.getLog().warn(string);
            } else {
              ViolationCommentsMojo.this.getLog().info(string);
            }
          }

          @Override
          public void log(final Level level, final String string, final Throwable t) {
            if (level == Level.FINE) {
              ViolationCommentsMojo.this.getLog().debug(string, t);
            } else if (level == Level.SEVERE) {
              ViolationCommentsMojo.this.getLog().error(string, t);
            } else if (level == Level.WARNING) {
              ViolationCommentsMojo.this.getLog().warn(string, t);
            } else {
              ViolationCommentsMojo.this.getLog().info(string);
            }
          }
        };

    Set<Violation> allParsedViolations = new TreeSet<>();
    for (final ViolationConfig configuredViolation : this.violations) {
      final Set<Violation> parsedViolations =
          violationsApi()
              .withViolationsLogger(violationsLogger)
              .findAll(configuredViolation.getParser()) //
              .inFolder(configuredViolation.getFolder()) //
              .withPattern(configuredViolation.getPattern()) //
              .withReporter(configuredViolation.getReporter()) //
              .violations();
      if (this.minSeverity != null) {
        allParsedViolations = Filtering.withAtLEastSeverity(allParsedViolations, this.minSeverity);
      }
      allParsedViolations.addAll(parsedViolations);
    }

    try {
      final TokenType tokenType = this.apiTokenPrivate ? TokenType.PRIVATE : TokenType.ACCESS;
      final Integer mergeRequestIidInteger = Integer.parseInt(this.mergeRequestIid);
      violationCommentsToGitLabApi()
          .setViolationsLogger(violationsLogger)
          .setHostUrl(this.gitLabUrl)
          .setProjectId(this.projectId)
          .setMergeRequestIid(mergeRequestIidInteger)
          .setApiToken(this.apiToken)
          .setTokenType(tokenType)
          .setCommentOnlyChangedContent(this.commentOnlyChangedContent) //
          .withShouldCommentOnlyChangedFiles(this.commentOnlyChangedFiles) //
          .setCreateCommentWithAllSingleFileComments(
              this.createCommentWithAllSingleFileComments) //
          .setCreateSingleFileComments(this.createSingleFileComments) //
          .setIgnoreCertificateErrors(this.ignoreCertificateErrors) //
          .setViolations(allParsedViolations) //
          .setShouldKeepOldComments(this.keepOldComments) //
          .setShouldSetWIP(this.shouldSetWip) //
          .setCommentTemplate(this.commentTemplate) //
          .setProxyServer(this.proxyServer) //
          .setProxyUser(this.proxyUser) //
          .setProxyPassword(this.proxyPassword) //
          .setMaxNumberOfViolations(this.maxNumberOfComments) //
          .toPullRequest();
    } catch (final Exception e) {
      this.getLog().error(e.getMessage(), e);
    }
  }
}
