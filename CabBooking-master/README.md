# Cab Booking Application :- Object oriented Programming course's Capstone Project

# Table of Contents
1. [UML](#UML)
2. [Contributing](#Contributing)
3. [Style check](#StyleCheck)
4. [Continuous Integration](#ContinuousIntegration)

<a name="UML"></a>
# UML Sequence Diagrams
1. Login: https://www.lucidchart.com/documents/view/d8309bf3-b10c-427f-9e34-758ba221632b/0_0
2. Sign Up: https://www.lucidchart.com/documents/view/49c23443-1643-41bc-8952-106fe0bc1623/0_0
3. Cab booking: https://www.lucidchart.com/documents/view/5d783536-934e-4248-91ff-8003453527ea/0_0
4. Use Case: https://www.lucidchart.com/documents/view/f32da1ad-ae09-4e5d-815c-56202c0dedb2/0_0

<a name="Contributing"></a>
# CONTRIBUTING
<a name="Initial setup"></a>
## INITIAL SETUP
1. Fork the repository from the [main repo](https://github.com/VikramjeetD/CabBooking/tree/master) to your github. 
2. Clone the forked repo to your local machine.
   1. From the terminal, navigate to the folder in which to clone the repository. 
   2. Initialize a new git repository using `git init`.
   3. Copy the `git clone` command from your github fork and paste it in terminal (`git clone <url>`). Your local copy is now associated with the remote copy on your github.
3. Set the upstream remote: `git remote add upstream <https://github.com/VikramjeetD/CabBooking.git>` . NOTE: This URL needs to be that of the repo from which you originally forked your repo. 
4. Verify remotes: `git remote -v`. 
   You should see something like: 
```
> origin git@github.com:YOUR_USERNAME/CabBooking.git (fetch)
> origin  git@github.com:YOUR_USERNAME/CabBooking.git (push)
> upstream git@github.com:MY_USERNAME/CabBooking.git (fetch)
> upstream git@github.com:MY_USERNAME/CabBooking.git (push)
```

You have now finished setting up the local repository. 

<a name="Writing and adding code"></a>
## Writing and adding code
It is recommended you do not work on the `master` branch at any time. Create a new branch, work on it, and start PRs from that branch. 

### Create a branch
1. On terminal, `git checkout -b <name_of_your_new_branch>`. This creates a new local branch, and switches you to the new branch. (use `git checkout <branch_name>` to switch to some other branch)
2. Then `git push origin <name_of_your_new_branch>`. This pushes the local changes on the new branch to the `origin` remote, i.e., creates a new branch on remote. This branch will now also be visible from your github repo. 

### Committing and pushing your code
Committing is like saving your changes, except it is for git. You should commit your code at regular intervals. 
1. Select the files you want to commit: `git add <file_name_1> <file_name_2> ... <file_name_last>`. To add all files, use `git add --all`. This puts the given files for staging. 
2. Check whether the correct files are staged for commit using `git status`.
3. Commit the files using `git commit -m <commit_message>`. This "saves" the files locally.
4. Push the changes to `origin` using `git push origin <branch_name>`.
Your changes will now be visible from your github repo.

WARNING: You must commit your changes before switching branches, or all progress on the existing branch is lost. There is no warning before switching, even if you have not commited your changes.

### Starting a pull request (PR)
When you are ready for your changes to be incorporated into the `upstream master` branch, you need to do it through a pull request.
1. From `origin`, start a PR, select the branch to be merged and the branch to which the merge needs to be made.
2. Start the PR. Any required changes to the code can now be discussed.
3. PR will be merged.

NOTE: Pushing directly to `master` is not a good practice and has been disabled for this repository.  

### Resolving merge conflicts
It might happen that someone else has modified the same file that you did. Git cannot resolve which version to include, and throws a merge conflict, that must be manually resolved.
You will see something like:
```
<<<<<<< your branch
your code
=======
existing code
>>>>>>> existing branch
```

Edit the file to keep needed parts of the code, and remove the conflict markers, (`<<<<<<`, `=======`, `>>>>>>>`). Do this for every file.
Click on commit changes.

WARNING: When you resolve a merge conflict on GitHub, the entire base branch (branch to which you are merging) of your pull request is merged into the head branch (the branch from which you are merging), even if the head branch is the default branch of your repository, like master, or a protected branch. Make sure you really want to commit to this branch, as this could potentially break your code.

### Keeping your local master branch upto date with upstream master
Changes will keep happening on `upstream master`, and they need to be done on your local branch and `origin` for compatibility, at regular intervals.
1. On terminal: `git checkout master`.
2. On terminal: `git fetch upstream`. This fetches from the `upstream` remote and stores the master locally in `upstream/master`.
3. On terminal: `git merge upstream/master`. This merges the `upstream master` branch into your local `master` branch. It is now up to date.
4. On terminal: `git push origin master`. This pushes the changes to the `origin` remote's `master` branch.
Your code is now up to date with the latest working version on `upstream master`.

<a name="StyleCheck"></a>
# STYLECHECK
A list of rules that your code is evaluated on according to the [Sun Coding Guidelines](https://checkstyle.sourceforge.io/sun_style.html).  
NOTE: Some of the rules have been disabled.  
NOTE: Most of the dedicated Java IDEs (IntelliJ IDEA and Eclipse) by default have the feature of performing a lot of checks automatically. Try to remove all possible warnings from your code, it will rectify a lot of these stylecheck violations.

To run the following tests, you can run `mvn checkstyle:check --batch-mode` on your local terminal, provided you have Maven installed. Also, stylecheck will not work unless your code compiles successfully.

The script checks for:
* New line at end of file.
* There should be no tab ('\t') characters in the source code. Note that most modern IDEs and text editors (for coding) automatically do this for you. We can decide to turn this off if this is a problem, but it is important to keep the spacing uniform accross all devices.
* No trailing whitespace at end of a line.
* All constants, local `final` variables, and variables declared in `catch` block must be written in ALL CAPS.
* All constants must be declared `static` and `final` unless it is part of an interface.
* All other variables (static, non static), and method names should begin with a lowercase character (preferably it should be in camelCase but it is not checked)
* All class names should begin with an Uppercase letter (Also, preferably in camelCase)
* No redundant and unused imports. (Also highlighted by IDEs)
* Length of line should not exceed 200 characters. Make it fit on the screen, write it on a new line.
* Method should not be more than 200 lines long, and should not contain more than 15 parameters. If it does, you can probably refactor your code into smaller pieces. 
* No spaces after left parenthesis and before right parenthesis.
* Whitespace after commas, and semicolons (except at end of line).
* Whitespace before and after operators.
* No empty methods, constructors, loops, or catch blocks.
* Modifiers should follow [this](https://checkstyle.sourceforge.io/apidocs/com/puppycrawl/tools/checkstyle/checks/modifier/ModifierOrderCheck.html) order. Eg: `public static final` and not `static public final`.
* No redundant modifiers (highlighted by IDEs).
* No hanging nested blocks (`{...}`) except inside `switch-case`.
* Braces (`{...}`) for loops and `if-else` blocks.
* Braces for `try-catch-finally` and `if-else`  blocks should be on the the same line as the next part of a multi block statement:
Eg: 
```
try {
...
} catch {
...
}
```
and not
```
try {
...
}
catch {
...
}
```
Same for if else.
However, single line multi block statements are allowed.
```
if (a > 0) {...} else {...}
```
This is allowed.
* No empty statements (like `if (condition);`) (checked by IDEs)
* Local variables should not shadow other fields. You should be fine if you give unique names to all variables. (checked by IDEs
* No inner assignments (`String s = Integer.toString(i = 2);`). This increases readability. (Suggested by IDEs)
* Simplify boolean expressions and boolean returns (Eg: `b || true` is same as `true`) (checked by IDEs).
* Only `static final`, immutable or annotated class members may be public, remaining have to be private. (Protected can be enabled if needed). (checked by IDEs)
* No C-style array declarations, only java style is allowed (`int[] a` is allowed, `int a[]` is not) (checked by IDEs).
* Uppercase l (L) to define `long` constants (`400L` and not `400l`), as l looks a lot like 1;

<a name="ContinuousIntegration"></a>
# CONTINUOUS INTEGRATION (TravisCI)

This repository is configured to work with TravisCI, a continuous integration tool. There will be two builds for each PR:
* Branch build
* Pull Request build

The branch build builds the code in the head branch of the PR.
The Pull Request build builds the code after merging the PR.

For both builds, 2 commands are run
1. `mvn clean verify`: Builds (compiles) the code and performs JUnit tests if any. If this build fails, it means the code did not compile successfully, or one or more unit tests failed.
2. `mvn checkstyle:check --batch-mode`: Performs the stylechecks according to the rules listed above.

If the Branch and PR builds both fail, that means your code does not get compiled successfully, or is not working as expected.  
If the Branch build passes but the PR build fails, it means you have made breaking changes in your code. It is not compatible with the existing code, and breaks it.
It is recommended that both builds pass for the PR to be fit to merge.
