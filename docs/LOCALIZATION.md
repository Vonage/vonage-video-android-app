# Multi-Language Support

The app is fully prepared for internationalization using Android's **string resources** (`strings.xml`). All user-facing strings are surfaced through `stringResource()` in Jetpack Compose, making it straightforward to add support for new languages.

## How it works

- All user-facing strings are defined in `app/src/main/res/values/strings.xml`.
- Jetpack Compose screens reference strings via `stringResource(R.string.key_name)` rather than hardcoded literals, so the Android resource system automatically selects the correct locale at runtime.

## Adding a new language

1. Create a new values directory for your locale inside `app/src/main/res/`:

   ```
   app/src/main/res/values-<language-code>/strings.xml
   ```

   For example, for French:

   ```
   app/src/main/res/values-fr/strings.xml
   ```

   For regional variants (e.g., Canadian French):

   ```
   app/src/main/res/values-fr-rCA/strings.xml
   ```

2. Copy `app/src/main/res/values/strings.xml` into the new directory and translate all string values. Keep the `name` attributes identical to the originals.

3. Build and run the app. Android will automatically select your new translations when the device locale matches.

## Tips

- Use Android Studio's **Translations Editor** (open any `strings.xml` → click the globe icon) for a side-by-side view of all locales.
- Strings that should never be translated (e.g., brand names, URLs) can be marked with `translatable="false"` in the base `strings.xml`.
- Plurals and quantity strings use the `<plurals>` element — see the [Android plurals documentation](https://developer.android.com/guide/topics/resources/string-resource#Plurals) for details.
