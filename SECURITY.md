# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |

---

## Reporting a Vulnerability

The safety and security of users is paramount. If you discover a security vulnerability, sensitive token exposure, or security-sensitive defect:

1. **Do NOT file a public GitHub issue.**
2. Please send a detailed security report via email to **adityachauhan1st@gmail.com**.
3. Include the following details in your report:
   - Description of the vulnerability or security flaw
   - Steps to reproduce or proof of concept
   - Potential impact
   - Proposed mitigation (if known)

You will receive an acknowledgment within 48 hours, followed by updates regarding remediation and release timing.

---

## Sensitive Configuration & API Keys

- **Never commit `.env` or keystore files containing real private passwords.**
- Always use `.env.example` as a safe placeholder template.
- Secrets on GitHub Actions should always be injected via **GitHub Repository Secrets**.
