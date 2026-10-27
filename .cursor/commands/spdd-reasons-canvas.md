---
name: /spdd-reasons-canvas
id: spdd-reasons-canvas
category: Development
description: Generate REASONS-Canvas structured prompts from business context without external template
---

Generate implementation-ready structured prompts using the built-in REASONS-Canvas framework (Requirements, Entities, Approach, Structure, Operations, Norms, Safeguards).

**Input**: Business context/requirement description after `/spdd-reasons-canvas`

Input can be provided in two ways:

1. **Text description**: Direct text describing the requirement
2. **File/folder reference**: Using `@` to reference files or folders containing requirements

**Examples**:

```
# Text description
/spdd-reasons-canvas Implement user registration functionality, supporting email verification and mobile phone binding

# File reference
/spdd-reasons-canvas @requirements/user-registration.md

# Combined (text + file reference)
/spdd-reasons-canvas @requirements/user-registration.md additionally requires support for third-party OAuth login

# Multiple file references
/spdd-reasons-canvas @requirements/user-registration.md @docs/api-spec.yaml
```

**Steps**

1. **Validate and consolidate business context**

   a. **If business context is missing**, use the **AskUserQuestion tool** (open-ended, no preset options) to ask:
   - "Please provide the business context or requirement description (you can use text, @file references, or both)"

   **IMPORTANT**: Do NOT proceed without business context input.

   b. **If input contains `@` file/folder references**:
   - Read ALL referenced files completely using the Read tool
   - For folder references, read all relevant files within the folder (`.md`, `.txt`, `.yaml`, `.json`, etc.)
   - Consolidate all file contents into a unified business context

   c. **Combine all context sources**:
   - Merge text descriptions with file contents
   - Preserve the complete information from all sources
   - Do NOT summarize or truncate - maintain full context integrity

   **Context Integrity Check**:
   - Verify all `@` references were successfully read
   - If any file cannot be read, report the error and ask user to provide alternative
   - Confirm the consolidated context contains sufficient information to proceed

2. **Read norms (mandatory — do this before generating or writing the canvas)**

   **CRITICAL**: You MUST read norm files using the Read tool before populating **N — Norms** or writing `spdd/prompt/*.md`. Do NOT skip, partially read, rely on memory from prior sessions, or use a hardcoded norm file list.

   a. Read `spdd/norms/README.md` completely — it is the **norms registry** and the only source of truth for which norm files exist

   b. **Derive norm files from the registry**: parse the **Norm index** table (and any other `spdd/norms/*.md` links in README) to build the list of norm file paths. Do NOT assume or hardcode filenames — new norms added to the registry must be picked up automatically

   c. **Read derived norm files**: using registry topic descriptions and business context, read every applicable norm file from (b) completely with the Read tool. If feature scope is unclear, read **all** registry-listed norm files

   d. The **N — Norms** section MUST cite only `spdd/norms/...` paths you read in this step and MUST NOT invent standards that contradict those files

   **Norms read gate**: If README cannot be read, or any required derived norm file cannot be read, report the error and STOP — do not generate the canvas or write `spdd/prompt/*.md` until resolved. Exception: a norm file whose **Module** in the Norm index is not listed under `modules` in `template.yaml` is not rendered in this service. Skip it without stopping, and do not plan code that uses the types it describes.

3. **Read relevant codebase context**
   - Search for related existing implementations
   - Read relevant entity classes, services, controllers
   - Understand current architecture patterns
   - Identify existing data structures and APIs

4. **Apply the REASONS-Canvas Framework**

   Generate fully-populated content for each of the 7 stages using the built-in construction guidance:

   ***

   ### R - Requirements

   **Objective**: Extract core problem essence and fundamental goals

   **Output Format**:

   ```
   ## Requirements
   [Use concise verb phrases to describe the essence of requirements]
   ```

   **Construction Guidance**:
   - **Essence Extraction**: Abstract what fundamental problem to solve and what value to create for whom
   - **Boundary Definition**: Clarify the applicable scope and limitations
   - **Value Focus**: Highlight business value and user benefits
   - **Use Verb Phrases**: "Implement...", "Create...", "Design..."
   - **Avoid Feature Stacking**: Don't list specific functions, abstract essential problems

   **Quality Standards**:
   - Core requirements summarizable in one sentence
   - Reflect business value rather than technical implementation
   - Clear problem boundaries and constraints

   ***

   ### E - Entities

   **Objective**: Build clear business entity relationship models

   **Output Format**:

   ````
   ## Entities
   ```mermaid
   classDiagram
   direction TB

   class [CoreEntity] {
       +[AttributeType] [attributeName]
       +[Method]()
   }

   class [RelatedEntity] {
       +[AttributeType] [attributeName]
   }

   class [RequestDTO] {
       +[AttributeType] [attributeName]
   }

   class [ResponseDTO] {
       +[AttributeType] [attributeName]
   }

   [CoreEntity] "[cardinality]" -- "[cardinality]" [RelatedEntity] : [relationshipDesc]
   [RequestDTO] --> [CoreEntity] : creates
   [CoreEntity] --> [ResponseDTO] : maps to
   ```
   ````

   **Construction Guidance**:
   - **Entity Identification**: Identify core business entities, supporting entities, DTO objects
   - **Attribute Modeling**: Define key attributes using "type+name" format
   - **Relationship Modeling**: Clarify relationship types (1:1, 1:N, N:M) and business semantics
   - **Interface Design**: Include key methods and static factory methods
   - **Data Flow**: Reflect complete flow of request→processing→response

   **Conservative Constraints** (CRITICAL):
   - **Prohibit Unnecessary Refactoring**: If existing simple data types (like `List<String>`) can meet requirements, strictly prohibit creating complex entity wrappers
   - **Existing Implementation Priority**: If current data structures can meet requirements, existing implementation must remain unchanged
   - **Function-Driven Changes**: Only consider structural adjustments when clear functional requirements cannot be implemented through existing structures
   - **Gradual Improvement**: Prioritize extending based on existing structures rather than rebuilding
   - **Backward Compatibility**: Any structural changes must ensure backward compatibility

   **Quality Standards**:
   - Focus on current task flows
   - Clear and accurate entity relationships
   - Maintain simplicity of existing implementations
   - Avoid over-abstraction and unnecessary complexity

   ***

   ### A - Approach

   **Objective**: Provide high-level solution strategies and architectural approaches

   **Output Format**:

   ```
   ## Approach
   1. [Solution Category]:
      - [High-level strategy description]
      - [Architecture pattern or approach]
      - [Key design decisions and rationale]

   2. [Technical Implementation]:
      - [Framework or technology choice]
      - [Integration pattern]
      - [Performance and security considerations]
      - [Global exception handling strategy with GlobalExceptionHandler]

   3. [Business Logic]:
      - [Core business rules]
      - [Validation and error handling strategy]
      - [Workflow and process design]
   ```

   **Construction Guidance**:
   - **Categorical Organization**: Organize by solution categories (API design, data processing, exception handling)
   - **Architecture Decisions**: Provide key technical architecture choices and design patterns
   - **Best Practices**: Combine industry standards and experience summaries
   - **Decision Rationale**: Explain why specific solutions were chosen
   - **Risk Assessment**: Identify potential risks and response strategies

   **Quality Standards**:
   - Solutions have operability
   - Cover key technical decisions
   - Reflect architectural thinking

   ***

   ### S - Structure

   **Objective**: Define technical architecture and component dependency relationships

   **Output Format**:

   ```
   ## Structure

   ### Inheritance Relationships
   1. [Interface] interface defines [functionality description]
   2. [Implementation] implements [Interface] interface
   3. [DomainModel] extends [BaseModel] class
   4. [BusinessException] extends RuntimeException class

   ### Dependencies
   1. [ComponentA] calls [ComponentB]
   2. [Service] depends on [Repository] and [ExternalService]
   3. [Controller] injects [Service] and [ValidationService]

   ### Layered Architecture
   1. Controller Layer: [ResponsibilityDescription]
   2. Service Layer: [ResponsibilityDescription]
   3. Repository Layer: [ResponsibilityDescription]
   4. Data Access Layer: [ResponsibilityDescription]
   5. Exception Handling Layer: [GlobalExceptionHandler for unified error handling]
   ```

   **Construction Guidance**:
   - **Inheritance System**: Clarify inheritance relationships of interfaces, abstract classes, and implementation classes
   - **Dependency Chain**: Define call and dependency relationships between components
   - **Layered Design**: Reflect clear layered architecture (Controller → Service → Repository → DAO)
   - **Responsibility Separation**: Responsibility boundaries and interaction interfaces of each layer
   - **Extension Interfaces**: Interfaces and extension points for future functionality expansion

   **Quality Standards**:
   - Clear architectural hierarchy
   - Reasonable dependency relationships
   - Support system extension

   ***

   ### O - Operations

   **Objective**: Transform abstract solutions into specific executable implementation tasks

   **Output Format**:

   ```
   ## Operations

   ### Create/Update [ComponentType] - [ComponentName]
   1. Responsibility: [Clear responsibility description]
   2. Attributes:
      - [attributeName]: [Type] - [Description]
   3. Methods:
      - [methodName]([parameters]): [ReturnType]
        - Logic:
          - [Step-by-step implementation logic]
          - [Conditional logic and edge cases]
          - [Error handling approach]
   4. Annotations: [Required annotations]
   5. Constraints: [Validation rules and business constraints]

   ### Implement [ServiceType] - [ServiceName]
   1. Interface Definition: [Interface methods and contracts]
   2. Core Methods: [methodName]([parameters]): [ReturnType]
      - Input Validation: [Input validation rules]
      - Business Logic: [Core business logic steps]
      - Exception Handling: [Exception handling strategy]
      - Return Value: [Return value construction]
   3. Dependency Injection: [Required dependencies]
   4. Transaction Management: [Transaction boundary definition]

   ### Create Exception Handler - GlobalExceptionHandler
   1. Responsibility: Unified handling of global exceptions
   2. Exception Types:
      - BusinessException: [Business logic exceptions]
      - ValidationException: [Input validation exceptions]
      - SystemException: [System-level exceptions]
   3. Methods:
      - handleBusinessException(BusinessException): ResponseEntity<ErrorResponse>
      - handleValidationException(ValidationException): ResponseEntity<ErrorResponse>
   4. Annotations: @RestControllerAdvice, @ExceptionHandler
   5. Response Format: Unified error response structure

   ### Create Business Exception - [ExceptionName]
   1. Inheritance: extends RuntimeException or BusinessException
   2. Attributes:
      - errorCode: String - Business error code
      - errorMessage: String - Detailed error description
   3. Constructors: Multiple constructors for different scenarios
   4. Usage Scenarios: [When to throw this exception]

   ### High-level tests (Gherkin)
   (only when the input included an analysis file)
   Cover: main requirement/feature paths, important edge cases, important user decisions/clarifications.
   Feature: [capability]
     Scenario: [one behavior]
       Given [precondition]
       When [action]
       Then [observable outcome]

   | Feature / Scenario | Test class | Method |
   | --- | --- | --- |
   | [Feature] / [Scenario] | [*IT or *Test] | [when...Then...] |
   ```

   **Construction Guidance**:
   - **Based on First Four Stages**: Strictly based on complete context of R, E, A, S
   - **Task Classification**: Group by functional modules or component types
   - **Implementation Details**: Include specific code specifications, configuration requirements, business logic
   - **Execution Order**: Organize task execution order based on dependency relationships
   - **Single Responsibility**: Each task has clear responsibilities and boundaries
   - **Verifiability**: Each task has clear completion criteria
   - **Logical Rigor**: Ensure task orchestration is based on business models, avoid logical loopholes
   - **Gherkin scenarios (analysis input)**: When the input included an analysis file, add a **High-level tests (Gherkin)** subsection. Derive N `Feature:` / `Scenario:` blocks in business language covering (1) **main requirement/feature paths**, (2) **important edge cases**, (3) **important user decisions/clarifications** recorded in the analysis. Include a 1:1 Feature/Scenario → test class/method table. Add the task of implementing them; each test method’s Javadoc copies its Scenario verbatim.

   **Quality Standards**:
   - Tasks can be executed directly
   - Cover complete implementation
   - Accurate and specific details

   ***

   ### N - Norms

   **Objective**: Define unified coding standards and common implementation patterns for **this repository** (odm-platform-adapter-observer-blindata)

   **Mandatory context**: Step 2 MUST already be complete — norm files were derived from `spdd/norms/README.md` and read via the Read tool. Summarize only norms that apply to this feature; link `spdd/norms/...` paths so `/spdd-generate` can re-read them. Do not invent parallel standards that contradict those files.

   **Output Format**:

   ```
   ## Norms
   1. Annotation Standards: [Specific annotation requirements for different component types]
   2. Dependency Injection: [Dependency injection patterns and best practices]
   3. Exception Handling:
      - Custom exception type definitions and inheritance relationships
      - Business exception class creation standards:
        * Inherit RuntimeException or custom BusinessException base class
        * Must include errorCode and errorMessage
        * Provide multiple constructor methods
        * Classify by business domain
      - Unified error response format (ErrorResponse DTO)
      - Logging and exception tracking mechanisms
   4. Data Validation: [Common validation patterns and rules]
   5. Logging: [Logging standards and patterns]
   6. Documentation Standards: [Documentation and comment standards]
   ```

   **Construction Guidance**:
   - **Start from the norms registry**: Norm files come from `spdd/norms/README.md` index — summarize applicable norms in the prompt; link paths (`spdd/norms/...`) so `/spdd-generate` can re-read them
   - **Standardization**: Define unified coding standards and configuration patterns
   - **Reusability**: Extract reusable common implementation patterns
   - **Consistency**: Ensure all components follow the same standards
   - **Quality Assurance**: Built-in validation and verification mechanisms
   - **Best Practices**: Reflect industry best practices

   **Quality Standards**:
   - Clear and specific standards
   - Easy to execute and check
   - Reflect best practices

   ***

   ### S - Safeguards

   **Objective**: Define clear boundary conditions and quality standards

   **Output Format**:

   ```
   ## Safeguards
   1. Functional Constraints: [Functional requirements and limitations with specific criteria]
   2. Performance Constraints: [Performance requirements with measurable metrics]
   3. Security Constraints: [Security requirements and compliance standards]
   4. Integration Constraints: [Integration limitations and compatibility requirements]
   5. Business Rule Constraints: [Business rule validation with specific conditions]
   6. Exception Handling Constraints:
      - Business exceptions must include clear error codes and error messages
      - Exception types must be classified by business domain
      - Exception information must not expose sensitive system internal information
      - All business exceptions must be handled by GlobalExceptionHandler
   7. Technical Constraints: [Technical implementation restrictions]
   8. Data Constraints: [Data validation rules and format requirements]
   9. API Constraints: [API design standards and interface contracts]
   ```

   **Construction Guidance**:
   - **Clear Boundaries**: Clearly define what can and cannot be done
   - **Verifiability**: Constraint conditions should be verifiable
   - **Completeness**: Cover all aspects including functionality, performance, security, integration
   - **Practicality**: Constraints should help improve code quality and system stability
   - **Quantified Standards**: Provide quantifiable standards and metrics whenever possible

   **Quality Standards**:
   - Clear constraint conditions
   - Verifiable
   - Complete coverage

5. **Construct the final structured prompt**

   Create a comprehensive, ready-to-implement prompt with:

   a. **Header Section**:

   ```
   # [Derived Requirement Title]
   ```

   b. **All 7 REASONS Sections - Fully Populated**:
   - `## Requirements` - Fully populated
   - `## Entities` - With Mermaid class diagram
   - `## Approach` - With solution strategies
   - `## Structure` - With architecture definition
   - `## Operations` - With specific implementation tasks
   - `## Norms` - With coding standards
   - `## Safeguards` - With constraints

   **DO NOT include**:
   - Business Context section (the original requirement text)
   - Framework metadata (Objective, Construction Guidance, Quality Standards)
   - Generation timestamp or framework name

   **ONLY include**: The structured content generated by analyzing the business context.

   c. **Implementation readiness**:
   - The final prompt should be immediately actionable
   - All sections should be fully populated with specific details
   - No placeholders or "TODO" items
   - Clear, executable implementation tasks in Operations section

6. **Save the fully-populated structured prompt to file**

   **CRITICAL — norms gate before write**: Do NOT call Write (or create `spdd/prompt/<file-name>.md`) unless Step 2 is complete — `spdd/norms/README.md` was read, applicable norm files were derived from its registry index, and each required derived file was read with the Read tool in this session. Writing the canvas without reading norms is forbidden.

   a. **Derive file name**: `{JIRA}-{TIMESTAMP}-[{ACTION}]-{scope}-{description}.md`
   - **JIRA**: Extract from business context if mentioned, otherwise use `GGQPA-XXX`
   - **TIMESTAMP**: `YYYYMMDDHHmm` (current time)
   - **ACTION**: Infer from business context - `[Feat]`, `[Fix]`, `[Refactor]`, `[Test]`, `[Docs]`
   - **scope**: Infer from context - `api`, `service`, `repo`, `bq`, `db`, `util` (optional)
   - **description**: Derive from business context - kebab-case, < 10 words

   Examples:
   - `GGQPA-XXX-202603061530-[Feat]-api-user-registration.md`
   - `GGQPA-169-202603061530-[Fix]-service-payment-validation.md`

   b. **Create directory and write file**:
   - Ensure directory `spdd/prompt/` exists under the project root (create if not)
   - Write the complete, fully-populated structured prompt to `spdd/prompt/<file-name>.md`

   c. **Show summary to user**:

   ```
   ✅ REASONS-Canvas prompt generated and saved to `spdd/prompt/<file-name>.md`

   📚 Norms read (Step 2): registry (`spdd/norms/README.md`) + [list applicable norm files derived from registry index]

   📋 Generated sections:
   - Requirements: [1-line summary]
   - Entities: [entity count] entities with relationships
   - Approach: [main approach summary]
   - Structure: [architecture pattern]
   - Operations: [task count] implementation tasks
   - Norms: [key standards]
   - Safeguards: [constraint count] constraints defined
   ```

7. **Ask for confirmation to proceed**

   > "The REASONS-Canvas structured prompt is ready. Would you like me to proceed with the implementation?"

**Output**

A fully-populated, implementation-ready REASONS-Canvas structured prompt saved to `spdd/prompt/<file-name>.md`, then implementation upon user confirmation.

**Guardrails**

- **CRITICAL — norms before canvas write**: MUST read `spdd/norms/README.md` (norms registry), derive applicable norm files from its **Norm index** table (never hardcode filenames), and read each required derived file with the Read tool in this session BEFORE populating **N — Norms** or writing `spdd/prompt/*.md`. Never write the canvas file from memory or without completing Step 2.
- **CRITICAL**: Do NOT just output section headers - you MUST analyze business context and generate fully-populated content for all 7 REASONS stages
- Do NOT proceed without business context input
- Do NOT include framework metadata (Objective, Construction Guidance, Quality Standards) in the final prompt
- Do NOT leave placeholders or TODO items - generate complete, specific content
- Do NOT implement code before user confirms the structured prompt
- File name MUST follow SPDD naming convention defined above
- Use `GGQPA-XXX` if JIRA ticket number cannot be extracted from context
- Always create `spdd/prompt/` directory if it does not exist
- Read codebase context when needed to generate accurate entity models and implementation tasks
- Ensure all sections are logically coherent and support the business requirement
- Operations section MUST contain specific, executable implementation tasks with detailed method signatures and logic
- **Conservative Entity Design**: Respect existing implementations, avoid unnecessary refactoring

**Context Integrity Guardrails**:

- **MUST read ALL `@` referenced files completely** - do NOT skip or partially read any referenced file
- **MUST read folder contents** when `@` references a folder - scan and read all relevant files
- **Do NOT summarize or truncate** referenced file contents - preserve full information
- **Verify all references resolved** - if any `@` reference fails to read, report error immediately
- **Combine all sources** - merge text descriptions with file contents into unified context
- **Preserve original intent** - do not interpret or modify the meaning of provided context
