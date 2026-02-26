# CI/CD Flow — Development Version and Branch Handling

This document describes the branching strategy, version numbering, and GitHub Actions workflows used to build, test, and release the OSGi Email project.

## Branch Strategy

The project follows a [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow) branching model. Every branch type has a specific role in the development lifecycle.

| Branch Pattern | Base | Purpose |
|---------------|------|---------|
| `develop` | — | Main development branch; contains the latest development sources |
| `feature/JNG-<N>_<summary>` | `develop` | New features for the next release |
| `release/<version>` | `develop` | Stabilization branches for a specific release (e.g., `release/1.0-beta1`) |
| `bugfix/JNG-<N>_<summary>` | `release/*` | Bug fixes applied during release stabilization |
| `support/JNG-<N>_<summary>` | `release/*` | Minor enhancements for a previous release |
| `hotfix/JNG-<N>_<summary>` | `master` | Emergency fixes applied to both `master` and `develop` |
| `master` | — | Latest released production-ready code |

```mermaid
gitGraph
    commit id: "initial"
    branch develop
    checkout develop
    commit id: "dev-1"
    branch feature/JNG-1
    commit id: "feat-1"
    commit id: "feat-2"
    checkout develop
    merge feature/JNG-1 id: "merge-feat"
    commit id: "dev-2"
    branch release/1.0-beta1
    commit id: "rc-1"
    branch bugfix/JNG-4
    commit id: "fix-1"
    checkout release/1.0-beta1
    merge bugfix/JNG-4 id: "merge-fix"
    checkout develop
    merge release/1.0-beta1 id: "merge-release"
    checkout main
    merge release/1.0-beta1 id: "release-1.0"
```

## Version Numbers

Versions follow [semantic versioning](https://semver.org/) with these rules:

| Event | Version Change |
|-------|---------------|
| Starting a `feature/` branch | No change |
| Starting a `release/` branch from `develop` | 2nd number (minor) is incremented on `develop` |
| `bugfix/` branches on `release/*` | No change — fixes are applied before the release is finalized |
| Starting a `support/` branch | 3rd number (patch) is incremented |
| Starting a `hotfix/` branch | 4th number (hotfix) is incremented |

## GitHub Actions Workflows

The CI/CD pipeline is composed of several interconnected workflows. Each workflow triggers downstream workflows via tags or branch events.

### build.yml — Main Build Pipeline

This is the primary workflow, triggered by pushes to `develop` and pull requests targeting `develop`, `master`, `increment/*`, or `release/*`.

```mermaid
flowchart TD
    trigger["Push on develop<br/>or PR on develop / master / release/*"]
    check{"Base branch?"}
    ver_release["Set version from pom.xml<br/>(without -SNAPSHOT)"]
    ver_dev["Set version:<br/>major.minor.qualifier.date_commitId_branch"]
    build["Build and deploy to Nexus"]
    tag["Create git tag v&lt;version&gt;"]
    check_inc{"increment/* or release/*?"}
    merge_tag["Create merge-pr/&lt;version&gt; tag"]
    trigger_merge["Trigger merge-pr-tagged.yml"]
    check_dev{"develop branch?"}
    changelog["Build changelog"]
    gh_release["Create GitHub pre-release"]

    trigger --> check
    check -->|"master, release/*"| ver_release
    check -->|"develop, increment/*"| ver_dev
    ver_release --> build
    ver_dev --> build
    build --> tag
    tag --> check_inc
    check_inc -->|Yes| merge_tag
    merge_tag --> trigger_merge
    check_inc -->|No| check_dev
    trigger_merge --> check_dev
    check_dev -->|Yes| changelog
    changelog --> gh_release
    check_dev -->|No| done["Done"]
    gh_release --> done
```

### merge-pr-tagged.yml — Pull Request Merger

Triggered when a `merge-pr/*` tag is pushed. Determines whether to merge to `master` (for release-quality versions) or squash back to `develop`.

```mermaid
flowchart TD
    trigger["Push on merge-pr/* tag"]
    extract["Extract version from tag"]
    check{"Version format?"}
    merge_master["Merge PR to master"]
    trigger_release["Trigger create-release-on-master.yml"]
    squash_dev["Squash PR to develop"]
    trigger_build["Trigger build.yml"]
    cleanup["Delete merge-pr/&lt;version&gt; tag"]

    trigger --> extract --> check
    check -->|"major.minor.qualifier"| merge_master --> trigger_release --> cleanup
    check -->|other| squash_dev --> trigger_build --> cleanup
```

### create-release-on-master.yml — Release Publisher

Triggered by pushes to `master`. Creates the final GitHub release with a changelog.

```mermaid
flowchart LR
    push["Push on master"] --> version["Get version from tag"]
    version --> changelog["Build changelog"]
    changelog --> release["Create GitHub release (latest)"]
```

### release.yml — Release Initiator

Manually triggered with a version parameter (or `auto` to use the POM version). Creates two pull requests — one for `master` with the release version and one for `develop` with the bumped next version.

```mermaid
flowchart TD
    trigger["Manual trigger with version"]
    check{"Version = 'auto'?"}
    from_pom["Use pom.xml version<br/>(without -SNAPSHOT)"]
    use_given["Use given version"]
    calc["Calculate next version<br/>(qualifier + 1)"]
    pr_master["Create PR on master<br/>with release version"]
    pr_develop["Create PR on develop<br/>with next version"]
    build1["Trigger build.yml"]
    build2["Trigger build.yml"]

    trigger --> check
    check -->|Yes| from_pom --> calc
    check -->|No| use_given --> calc
    calc --> pr_master --> build1
    calc --> pr_develop --> build2
```

## Development Rules

> **Important:** There is no commit without a ticket number. Every pull request and commit message must reference a JIRA ticket in the format `JNG-xxx`.

Issue tracking is managed in [JIRA](https://blackbelt.atlassian.net/jira/dashboards).
