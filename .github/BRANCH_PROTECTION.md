# Repository Protection & Access Control Guide

This guide details how this repository is configured so that **anyone can download the finished application**, but **no one can modify the repository directly without explicit owner authorization**.

---

## 🔒 1. How GitHub Access Works by Default

- **Download Access (Public)**: Anyone in the world can clone the repository, download source code, download release APKs, and view CI/CD workflows.
- **Write Access (Restricted)**: By default, only users explicitly added as Collaborators with "Write" or "Admin" roles can push commits. External visitors, forks, and non-collaborators **cannot** push or commit directly to this repository.

---

## 🛡️ 2. Enforcing Branch Protection on `main`

To ensure even collaborators or automated scripts cannot push unreviewed commits directly to `main`, configure **Branch Protection Rules** in GitHub:

1. In the GitHub repository, navigate to **Settings** > **Branches** (under *Code and automation*).
2. Click **Add branch ruleset** or **Add branch protection rule**.
3. Set **Branch name pattern** to: `main`
4. Enable the following settings:
   - ✅ **Require a pull request before merging**:
     - ✅ Require approvals: set to `1` (or more).
     - ✅ Dismiss stale pull request approvals when new commits are pushed.
     - ✅ Require review from Code Owners (uses `.github/CODEOWNERS`).
   - ✅ **Require status checks to pass before merging**:
     - ✅ Require branches to be up to date before merging.
     - Search and select: `Build APK` (from `.github/workflows/build-apk.yml`).
   - ✅ **Require conversation resolution before merging**:
     - Ensures all review comments must be resolved prior to merging.
   - ✅ **Do not allow bypassing the above settings**:
     - Enforces protection rules uniformly across all users.
   - ✅ **Restrict who can push to matching branches**:
     - Specify `@AdityaChuhan-bot` as the only authorized user.
5. Click **Save changes**.

---

## 👥 3. Pull Requests from External Users

When external contributors want to suggest improvements:
1. They must **Fork** the repository to their own GitHub account.
2. They make changes in their fork and open a **Pull Request**.
3. The Pull Request triggers automated CI testing (`build-apk.yml`) in a read-only container.
4. **Nothing can be merged into this repository** until `@AdityaChuhan-bot` reviews, tests, and explicitly approves the Pull Request.

---

## 📥 4. Enabling Direct APK Downloads for Everyone

To allow any user to download the compiled application without needing an account or compilation tools:
1. Every successful push to `main` triggers `.github/workflows/build-apk.yml`.
2. The workflow automatically publishes the latest compiled `app-debug.apk` directly to **GitHub Releases**.
3. Anyone can download the finished APK immediately at:
   `https://github.com/AdityaChuhan-bot/Nova-ai/releases/latest`
