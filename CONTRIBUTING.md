# Code Contribution Guidelines

QQueing is a free and open project and we love to receive contributions from our community — you! There are many ways to contribute, from writing tutorials or blog posts, improving the documentation, submitting bug reports and feature requests or writing code which can be incorporated into QQueing itself.

Bug reports
-----------

If you think you have found a bug in QQueing, first make sure that you are testing against the latest version of QQueing - your issue may already have been fixed. If not, search our [issues list](https://lab.ssafy.com/s10-final/S10P31A401/-/issues) on GitLab in case a similar issue has already been opened.


Feature requests
----------------

If you find yourself wishing for a feature that doesn't exist in QQueing, you are probably not alone. There are bound to be others out there with similar needs. Many of the features that QQueing has today have been added because our users saw the need.
Open an issue on our [issues list](https://lab.ssafy.com/s10-final/S10P31A401/-/issues) on GitLab which describes the feature you would like to see, why you need it, and how it should work.

Contributing code and documentation changes
-------------------------------------------

If you would like to contribute a new feature or a bug fix to QQueing,
please discuss your idea first on the GitLab issue. If there is no GitLab issue
for your idea, please open one. It may be that somebody is already working on
it, or that there are particular complexities that you should know about before
starting the implementation. There are often a number of ways to fix a problem
and it is important to find the right approach before spending time on a PR
that cannot be merged.

We add the `help wanted` label to existing GitLab issues for which community
contributions are particularly welcome, and we use the `good first issue` label
to mark issues that we think will be suitable for new contributors.

The process for contributing to any of the [QQueing repositories](https://lab.ssafy.com/s10-final/S10P31A401) is similar. Details for individual projects can be found below.

### Fork and clone the repository

You will need to fork the master QQueing code or documentation repository and clone it to your local machine. See
[GitLab help page](https://docs.gitlab.com/ee/user/project/repository/forking_workflow.html) for help.

Further instructions for specific projects are given below.

### Tips for code changes
Following these tips prior to raising a pull request will speed up the review
cycle.

* Add appropriate unit tests 
* Add integration tests, if applicable
* Lines that are not part of your change should not be edited (e.g. don't format
  unchanged lines, don't reorder existing imports)
* Add the appropriate [license headers](https://lab.ssafy.com/s10-final/S10P31A401/-/blob/dev/LICENSE?ref_type=heads) to any new files


### Submitting your changes

Once your changes and tests are ready to submit for review:

1. Test your changes

    Run the test suite to make sure that nothing is broken. 

2. Rebase your changes

    Update your local repository with the most recent code from the main QQueing repository, and rebase your branch on top of the latest master branch. We prefer your initial changes to be squashed into a single commit. Later, if we ask you to make changes, add them as separate commits.  This makes them easier to review.  As a final step before merging we will either ask you to squash all commits yourself or we'll do it for you.

3. Submit a pull request

    Push your local changes to your forked copy of the repository and [submit a merge request](https://docs.gitlab.com/ee/user/project/merge_requests/). In the merge request, choose a title which sums up the changes that you have made, and in the body provide more details about what your changes do. Also mention the number of the issue where discussion has taken place, eg "Closes #123".

Then sit back and wait. There will probably be discussion about the pull request and, if any changes are needed, we would love to work with you to get your merge request merged into QQueing. A yaml changelog entry will be automatically created, there is no need for external contributors to manually edit it, unless requested by the reviewer.

Please adhere to the general guideline that you should never force push
to a publicly shared branch. Once you have opened your merge request, you
should consider your branch publicly shared. Instead of force pushing
you can just add incremental commits; this is generally easier on your
reviewers. If you need to pick up changes from master, you can merge
master into your branch. A reviewer might ask you to rebase a
long-running merge request in which case force pushing is okay for that
request. Note that squashing at the end of the review process should
also not be done, that can be done when the pull request is [integrated
via GitLab](https://docs.gitlab.com/ee/user/project/merge_requests/squash_and_merge.html).
