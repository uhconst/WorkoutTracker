### Feature: Exercise Progression Graph

#### Overview

Add a feature that allows users to view a visual progression of the weight they have lifted for a specific exercise over time.

This feature is accessed via a long press on an exercise item, opening a context menu with an option **"View Graph"**.

---

#### Current State (Important)

* The app currently fetches **only the latest weight** for each exercise from Supabase
* However, Supabase already contains a table with the **full history of weight logs** for each exercise

---

#### Required Backend/Data Change

* Update the data fetching logic to retrieve **all historical weight entries** for a given exercise instead of only the latest one
* Query should:

    * Filter by `exercise_id`
    * Return all entries associated with that exercise
    * Include at minimum:

        * `date` (timestamp)
        * `weight` (number)
        * (optional) `reps`, `sets`
    * Be sorted in **ascending chronological order**

---

#### Entry Point

* User performs a **long press** on an exercise in the exercise list
* A **context menu / bottom sheet** appears
* One of the options is: `"View Graph"`
* Selecting this option opens a **modal or new screen** displaying the graph

---

#### Graph Type

* A **line chart with dots**
* Each dot represents a logged entry for that exercise
* Dots are connected chronologically by a line

---

#### Data Mapping

* **X-axis**: Time (based on log date or session index, ordered chronologically)
* **Y-axis**: Weight value (numeric, e.g. kg or lbs)
* Each data point corresponds to a single logged weight entry

---

#### Data Requirements

Each log entry should include:

* `date` (timestamp)
* `weight` (number)
* (optional, for tooltip)

    * `reps`
    * `sets`

---

#### Sorting

* Data must be sorted in **ascending chronological order** before rendering

---

#### UI Elements

**Header**

* Display the exercise name (e.g. "Bench Press")

**Graph**

* Line connecting all points
* Visible dots for each data point
* The **most recent point should be visually highlighted**

---

#### Interactions

**Tap on a data point**

* Show a tooltip with:

    * Weight (e.g. `82.5 kg`)
    * Date (formatted, e.g. `12 Mar 2026`)
    * (optional) reps and sets (e.g. `5 reps × 3 sets`)

---

**Horizontal scrolling**

* If the dataset is large, allow horizontal scrolling of the graph

---

#### Visual Behavior

* The line should be **smooth but accurate** (no misleading interpolation)
* Points should be clearly visible and tappable
* Do not artificially fill missing dates — only plot actual entries
* Sudden increases or decreases must be preserved (no normalization)

---

#### Empty State

If there is no data for the exercise:

* Display a message:
  `"No data yet — log this exercise to see your progress"`

---

#### Performance Considerations

* Handle large datasets efficiently (e.g. 100+ entries)
* Avoid unnecessary re-renders
* Consider downsampling if performance becomes an issue

---

#### Optional Enhancements (not required for MVP)

* Highlight personal records (max weight)
* Show improvement summary (e.g. "+10 kg since first entry")
* Time range filters (e.g. 1M / 3M / ALL)

---

#### Expected Outcome

The user should be able to quickly understand:

* Whether they are progressing over time
* Their latest performance
* Historical trends for a given exercise
