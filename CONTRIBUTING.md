# Contributing to Jarvis Mini (Nova AI)

Thank you for your interest in contributing to **Jarvis Mini**! We welcome bug fixes, UI improvements, hardware optimizations, and feature suggestions.

---

## Code of Conduct

Please be respectful, constructive, and collaborative in all discussions and pull requests.

---

## Getting Started

1. **Fork the repository** on GitHub.
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/<your-username>/Nova-ai.git
   cd Nova-ai
   ```
3. **Open the project in Android Studio** (Ladybug or newer).
4. **Create a local environment file**:
   ```bash
   cp .env.example .env
   ```
   Add a valid Gemini API key for testing AI features.

---

## Development Guidelines

### Code Style
- Follow official [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html).
- Adhere to **Material 3 Design Guidelines**.
- Interactive Jetpack Compose elements must meet touch target standards (minimum 48.dp) and include descriptive accessibility `contentDescription`s.
- Avoid placing large business logic directly in Composables; delegate to `JarvisViewModel`.

### Branching Strategy
- `main` is the stable release branch.
- **Direct Pushes Restricted**: Pushing directly to `main` is blocked. All changes require an approved Pull Request.
- Create feature branches with descriptive prefixes:
  - `feat/voice-command-expansion`
  - `fix/keystore-restore-script`
  - `docs/update-smart-speaker-guide`
  - `refactor/audio-visualizer-canvas`

---

## 🔒 Pull Request Review & Code Ownership

- Every Pull Request automatically requests review from the repository owner (`@AdityaChuhan-bot`) as defined in [`.github/CODEOWNERS`](.github/CODEOWNERS).
- Automated CI builds (`build-apk.yml`) verify compilation before any code can be merged.
- Only `@AdityaChuhan-bot` can merge changes into `main`. For details on the security model, refer to [`.github/BRANCH_PROTECTION.md`](.github/BRANCH_PROTECTION.md).

### Commit Message Conventions
We follow [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` A new user-facing feature
- `fix:` A bug fix
- `docs:` Documentation changes only
- `style:` Code formatting, missing semi-colons, etc. (no code logic changes)
- `refactor:` A code change that neither fixes a bug nor adds a feature
- `perf:` Performance improvements
- `build:` Build system or external dependency updates
- `ci:` CI configuration files and scripts

---

## Submitting a Pull Request

1. Verify the project builds locally without errors:
   ```bash
   ./gradlew assembleDebug
   ```
2. Push your feature branch to your GitHub fork:
   ```bash
   git push origin feat/your-feature-name
   ```
3. Open a Pull Request against the `main` branch.
4. Fill out the Pull Request template completely with details of what changed and test results.
