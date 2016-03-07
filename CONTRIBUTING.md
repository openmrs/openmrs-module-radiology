# Contributing to OpenMRS radiology module

## Resources for Getting Started

Your contributions are what will make this module a great addition to the OpenMRS ecosystem.

Since this project is an OpenMRS module we try to stick to the best practices developed by the OpenMRS community.

We therefore encourage you to read the great guides made available by OpenMRS before you get started contributing to this module:
* [OpenMRS Developers Guide](http://go.openmrs.org/newdev-web) 
* [Getting Started as a Developer](https://wiki.openmrs.org/x/MQAJ) wiki page.

## Reporting Bugs

1. Please check to see if you are running a version of OpenMRS compatible with this module [README](README#limitations)
2. Please check if you built this module from the latest commit to the master branch; the bug may already be resolved.
3. Please check https://issues.openmrs.org/browse/RAD if the bug has already been filed; if not feel free to open one yourself. You'll need to have an [OpenMRS ID](http://id.openmrs.org) to do so. You'll receive e-mail updates as the bug is investigated and resolved.

### Bug report contents

To help developers understand the problem, please include as much information as possible:

1. A clear description of how to recreate the error, including any error messages shown.
2. The version of OpenMRS you are using.
3. The commit you built the OpenMRS radiology module from which you have deployed into OpenMRS.
4. The type and version of the database you are using.
5. If applicable, please copy and paste the full Java stack trace.
6. If you have already communicated with a developer about this issue, please provide their name.

## Requesting New Features

1. We encourage you to discuss your feature ideas with our community before creating a New Feature issue in our issue tracker. Please login to [OpenMRS Talk](http://talk.openmrs.org), search for existing topics on the radiology module and if they fit to yours just join the discussion. If you do not find any topic related to your idea open up a new one.

2. Provide a clear and detailed explanation of the feature you want and why it's important to add.

3. If you're an advanced programmer, build the feature yourself (refer to the "Contributing (Step-by-step)" section below).

## Contributing (Step-by-step)

1. After finding a JIRA issue for the "Ready for Work" bug or feature on which you'd like to work, claim the issue by clicking the "Claim Issue" button. This will assign the issue to you and change its status to "In Progress".

2. [Fork the repo](http://help.github.com/fork-a-repo) on which you're working, clone your forked repo to your local computer, and set up the upstream remote:

        git clone git://github.com/YourGitHubUserName/openmrs-module-radiology.git
        git remote add upstream https://github.com/openmrs/openmrs-module-radiology.git

3. Checkout out a new local branch based on your master and update it to the latest. The convention is to name the branch after the current ticket, e.g. RAD-123:

        git checkout -b RAD-123 master
        git clean -df
        git pull --rebase upstream master

 > Please keep your code clean. Name your branch after the JIRA issue or other description of the work being done. If you find another bug, you want to fix while being in a new branch, please fix it in a separated branch instead.


4. Push the branch to your fork. Treat it as a backup.

        git push -u origin RAD-123

5. Code
 * follow the OpenMRS coding conventions, please read https://wiki.openmrs.org/display/docs/Coding+Conventions
   * take the existing code in the module as a guide as well!

 * add unit tests, we will not accept pull requests which lower the unit test coverage https://coveralls.io/github/openmrs/openmrs-module-radiology?branch=master
    * see https://wiki.openmrs.org/display/docs/Unit+Tests
    * see https://wiki.openmrs.org/display/docs/Unit+Testing+Conventions
    * see https://wiki.openmrs.org/display/docs/Module+Unit+Testing

 * follow the OpenMRS formatting/code style
    * you will need to configure Eclipse to use the [OpenMRSFormatter.xml](tools/src/main/resources/eclipse/OpenMRSFormatter.xml) provided by this module.
    * when you build this module *.java files will automatically be formatted by a formatter plugin. You can manually run the formatter plugin with ```mvn java-formatter:format```
    * for xml and javascript files use **control-shift-f** in Eclipse.
    * remove unused imports by using **control-shift-o** in Eclipse.


  > However, please note that **pull requests consisting entirely of style changes are not welcome on this project**. Style changes in the context of pull requests that also refactor code, fix bugs, improve functionality *are* welcome.

7. Commit

  For every commit please write a short (max 72 characters) summary in the first line followed with a blank line and then more detailed descriptions of the change. Use markdown syntax for simple styling. Please include any JIRA issue numbers in your summary.
  
        git commit -m "RAD-123: Put change summary here (can be a ticket title)"

  **NEVER leave the commit message blank!** Provide a detailed, clear, and complete description of your commit!

8. Issue a Pull Request

  Before submitting a pull request, update your branch to the latest code.
  
        git pull --rebase upstream master

  If you have made many commits, we ask you to squash them into atomic units of work. Most of tickets should have one commit only, especially bug fixes, which makes them easier to back port.

        git checkout master
        git pull --rebase upstream master
        git checkout RAD-123
        git rebase -i master

  Make sure all unit tests still pass:

        mvn clean package

  Push changes to your fork:

        git push -f

  In order to make a pull request,
  * Navigate to the modules repository you just pushed to (e.g. https://github.com/your-user-name/openmrs-module-radiology)
  * Click "Pull Request".
  * Write your branch name in the branch field (this is filled with "master" by default)
  * Click "Update Commit Range".
  * Ensure the changesets you introduced are included in the "Commits" tab.
  * Ensure that the "Files Changed" incorporate all of your changes.
  * Fill in some details about your potential patch including a meaningful title.
  * Click "Send pull request".
  * In JIRA, open your issue and click the "Request Code Review" button to change the issue's status to "Code Review". Add a comment linking to the URL of the pull request.


  Thanks for that -- we'll get to your pull request ASAP. We love pull requests!

## Responding to Feedback

  The OpenMRS team may recommend adjustments to your code. Part of interacting with a healthy open source community requires you to be open to learning new techniques and strategies; *don't get discouraged!* Remember: if the OpenMRS team suggests changes to your code, **they care enough about your work that they want to include it**, and hope that you can assist by implementing those revisions on your own.

  > Though we ask you to clean your history and squash commit before submitting a pull-request, please do not change any commits you've submitted already (as other work might be build on top).

## Other Resources

* If you haven't already, get an [OpenMRS ID](https://id.openmrs.org)
* Make sure you have a [GitHub account](https://github.com/signup/free)
* Submit or select a [JIRA issue](https://issues.openmrs.org/browse/RAD)
* Fork [openmrs-module-radiology](https://github.com/openmrs/openmrs-module-radiology/)

### Interact with the community

* Chat live with developers on [IRC](http://irc.openmrs.org) within #openmrs on freenode
* Join the [developers mailing list](https://wiki.openmrs.org/x/lwLn)
* Hang out with other community members at [OpenMRS Talk](http://talk.openmrs.org)

### Additional Resources

* [Issue tracker (JIRA)](https://issues.openmrs.org/browse/RAD)
* [General GitHub documentation](http://help.github.com/)
* [GitHub pull request documentation](http://help.github.com/send-pull-requests/)
