# Create: Deep Seas — Lava Fix

A small NeoForge mod for Minecraft **1.21.1** that fixes a bug in
[Create: Deep Seas](https://www.curseforge.com/minecraft/mc-mods/create-deep-seas)
(CDS) where players inside a hermetically sealed submarine still take fire/lava
damage while the submarine is submerged in lava, even though CDS correctly empties
the interior of lava.

> **Author:** Diyksfol  
> **License:** MIT  
> **Note:** This mod was developed with the help of an AI assistant; my own role
> was primarily that of a tester and director. The full source is provided here
> so anyone — including the Create: Deep Seas authors — may study, reuse, or
> integrate it.

---

## What it fixes

In Create: Deep Seas, a sealed submarine can pump out all fluid (including lava)
from its interior, letting you move around inside as if in open air. However, the
game's damage code still applied fire/lava damage to players standing in that
"emptied" space, because the damage checks did not account for the sealed sub.

This mod detects when a player is inside (or climbing out of) a sealed compartment
and suppresses the fire/lava damage and the on-screen fire overlay in that case.

### Specifically fixed
- Fire/lava damage while standing inside a sealed, lava-submerged submarine.
- Damage while standing in hatches/doorways at the lava surface (entry/exit).
- Damage while seated (Create seats), on ladders, or in doorways inside the sub.
- Endless fire ticks after the hull is breached and then re-sealed.
- The persistent fire overlay (flames at the bottom of the screen) while inside.

---

## What it does NOT fix (and why)

These behaviours are internal to Create: Deep Seas. Fixing them from an external
mod would require overriding core CDS systems and risks breaking the sealed-sub
mechanic itself, so they are intentionally left alone:

1. **Lava surface texture is still visible inside the sub.** CDS hides the *water*
   surface visually but not lava. This is a CDS rendering matter.
2. **Trapdoors don't work as climbable "ladders" when below the lava/water level
   inside the sub.** CDS hides the fluid, and vanilla's trapdoor-climb mechanic
   depends on a fluid being present, so the climb is disabled. Same effect occurs
   with water in CDS.
3. **Fire placed by the player inside the sub causes a fluid-like effect.** Standing
   in that fire applies slowness and, over lava, burning. This is a CDS interaction.
4. **Lava or water poured directly inside the sub deals no damage / no drowning.**
   This is a deliberate CDS feature (the interior is treated as sealed), not a bug.

If the Create: Deep Seas authors wish to address any of the above, they are welcome
to use this code as a starting point.

---

## Installation (players)

1. Install [NeoForge](https://neoforged.net/) for Minecraft 1.21.1.
2. Install [Create: Deep Seas](https://www.curseforge.com/minecraft/mc-mods/create-deep-seas)
   and its dependencies (Create, Sable, etc.).
3. Drop `submarinefix-1.0.0.jar` into your `mods/` folder.
4. Launch the game. That's it.

The mod is safe to install even without Create: Deep Seas — it simply does nothing
in that case.

---

## Building from source (developers)

This repository contains the **uncompiled source**. To build the jar yourself:

### Prerequisites
- JDK 21
- The Create: Deep Seas jar and the bundled Sable Companion jar (used as
  compile-only references — they are **not** redistributed here).

### Steps

1. Clone this repository: https://github.com/Diyksfol/create-deep-seas-lava-fix
2. Create a `libs/` folder in the project root and place two jars in it:
   - `create_submarine-2.1.6.jar` — from your Create: Deep Seas download.
   - `sable-companion-common-1.21.1-1.5.0.jar` — extracted from the CDS jar's
     `META-INF/jarjar/` folder (CDS bundles it).

   Your `build.gradle` already references these via `compileOnly files(...)`:
   ```groovy
   compileOnly files('libs/create_submarine-2.1.6.jar')
   compileOnly files('libs/sable-companion-common-1.21.1-1.5.0.jar')
   ```
   If your CDS version differs, update the filenames in `build.gradle` accordingly.

3. Build:
   ```bash
   ./gradlew build
   ```
   On Windows:
   ```bat
   .\gradlew.bat build
   ```

4. The compiled jar will be in `build/libs/`.

> These jars are third-party and are intentionally **not** included in this
> repository. You must supply them yourself from your own Create: Deep Seas install.

---

## How it works (technical overview)

The fix combines several layers:

- **`LivingIncomingDamageEvent` (server, primary):** cancels incoming
  `LAVA` / `IN_FIRE` / `ON_FIRE` / `HOT_FLOOR` damage when the player is inside a
  sealed compartment.
- **`Entity#baseTick` mixin (server):** suppresses the `lavaHurt()` call.
- **`LocalPlayer#baseTick` mixin (client):** suppresses the client-side
  `lavaHurt()` so no fire ticks are produced locally.
- **`ScreenEffectRenderer#renderFire` mixin (client):** hides the fire overlay
  while inside the sub.
- **`PlayerTickEvent.Post` (server):** clears any residual fire ticks each tick.

### Detection

To decide whether a player is "inside a sealed sub", the mod:

1. Iterates registered submarines via `CompartmentTracker.getSubsSnapshot()`.
2. Rejects subs whose world-space AABB (`getWorldAABB`) doesn't contain the player.
3. Transforms the player's world position into the sub's plot-local space via
   `subLevelAccess.logicalPose().transformPositionInverse(...)`.
4. Checks the resulting block against each sealed `Component`'s `internal()` and
   `hull()` sets from `CompartmentTracker.getCompartments(uuid)`.
5. Checks **both feet and eye positions** (so seated players, ladder-climbers and
   players in doorways are covered), and — when the player is on a climbable —
   additionally scans vertically to cover ladder shafts that extend above the hull.

This mirrors the internal logic CDS itself uses, but avoids the CDS public methods
that only work when called from *inside* a sub-level.

---

## Compatibility

- **Minecraft:** 1.21.1
- **Mod loader:** NeoForge 21.1+
- **Requires:** Create: Deep Seas (optional dependency; mod no-ops without it)

If you find a compatibility issue with another mod, please open an issue.

---

## License

Released under the **MIT License**. You are free to use, copy, modify, and
redistribute this code, including in other projects or mods, provided the original
attribution is retained. See [`LICENSE`](LICENSE) for the full text.

Anyone is welcome to reuse this code — including the authors of Create: Deep Seas,
should they wish to integrate the fix directly.

---
---

# 🇷🇺 Русская версия

# Create: Deep Seas — Lava Fix

Небольшой мод для NeoForge (Minecraft **1.21.1**), исправляющий баг в
[Create: Deep Seas](https://www.curseforge.com/minecraft/mc-mods/create-deep-seas)
(CDS), из-за которого игрок внутри герметичной субмарины всё равно получает урон
от огня и лавы, пока субмарина погружена в лаву — хотя CDS корректно откачивает
лаву из внутреннего пространства.

> **Автор:** Diyksfol  
> **Лицензия:** MIT  
> **Примечание:** мод разработан с помощью ИИ-ассистента; моя роль заключалась в
> основном в тестировании и постановке задач. Полный исходный код выложен здесь,
> чтобы любой — включая авторов Create: Deep Seas — мог изучить, переиспользовать
> или интегрировать его.

---

## Что исправляет

В Create: Deep Seas герметичная субмарина может откачивать всю жидкость (включая
лаву) из внутреннего пространства, позволяя свободно перемещаться внутри как по
воздуху. Однако код урона всё равно наносил игроку урон от огня/лавы в этом
«очищенном» пространстве, так как проверки урона не учитывали герметичную субмарину.

Этот мод определяет, когда игрок находится внутри (или выбирается из) герметичного
отсека, и в этом случае подавляет урон от огня/лавы и эффект пламени на экране.

### Конкретно исправлено
- Урон от огня/лавы внутри герметичной субмарины, погружённой в лаву.
- Урон в люках и дверных проёмах на уровне поверхности лавы (вход/выход).
- Урон в сиденьях (из Create), на лестницах и в дверных проёмах внутри субмарины.
- Бесконечное горение после нарушения и повторного восстановления герметичности.
- Постоянный эффект пламени (огонь внизу экрана) внутри субмарины.

---

## Что НЕ исправлено (и почему)

Эти особенности относятся к внутренней логике самого Create: Deep Seas. Их
исправление из внешнего мода потребовало бы переопределения базовых систем CDS и
могло бы сломать саму механику герметичной субмарины, поэтому они намеренно
оставлены без изменений:

1. **Текстура поверхности лавы по-прежнему видна внутри субмарины.** CDS визуально
   скрывает поверхность *воды*, но не лавы. Это вопрос рендеринга CDS.
2. **Люки не работают как лестницы, если находятся ниже уровня лавы/воды внутри
   субмарины.** CDS скрывает жидкость, а ванильная механика карабканья по люку
   зависит от наличия жидкости, поэтому карабканье отключается. Тот же эффект
   возникает с водой в CDS.
3. **Огонь, поставленный игроком внутри субмарины, вызывает эффект жидкости.**
   Находясь в этом огне, вы получаете замедление, а над лавой — горение. Это
   взаимодействие внутри CDS.
4. **Лава или вода, налитая непосредственно внутри субмарины, не наносит урона / не
   вызывает удушья.** Это намеренная особенность CDS (внутреннее пространство
   считается герметичным), а не баг.

Если авторы Create: Deep Seas захотят устранить что-либо из перечисленного, они
могут использовать этот код как отправную точку.

---

## Установка (для игроков)

1. Установите [NeoForge](https://neoforged.net/) для Minecraft 1.21.1.
2. Установите [Create: Deep Seas](https://www.curseforge.com/minecraft/mc-mods/create-deep-seas)
   и его зависимости (Create, Sable и т.д.).
3. Положите `submarinefix-1.0.0.jar` в папку `mods/`.
4. Запустите игру. Готово.

Мод безопасно устанавливать даже без Create: Deep Seas — в этом случае он просто
ничего не делает.

---

## Сборка из исходников (для разработчиков)

Этот репозиторий содержит **нескомпилированный исходный код**. Чтобы собрать jar
самостоятельно:

### Требования
- JDK 21
- Jar-файл Create: Deep Seas и встроенный в него Sable Companion jar (используются
  только для компиляции — здесь они **не** распространяются).

### Шаги

1. Склонируйте репозиторий.
2. Создайте папку `libs/` в корне проекта и поместите туда два jar-файла:
   - `create_submarine-2.1.6.jar` — из вашей загрузки Create: Deep Seas.
   - `sable-companion-common-1.21.1-1.5.0.jar` — извлечённый из папки
     `META-INF/jarjar/` внутри jar-файла CDS (CDS включает его в себя).

   В `build.gradle` они уже подключены через `compileOnly files(...)`:
   ```groovy
   compileOnly files('libs/create_submarine-2.1.6.jar')
   compileOnly files('libs/sable-companion-common-1.21.1-1.5.0.jar')
   ```
   Если ваша версия CDS отличается, обновите имена файлов в `build.gradle`.

3. Соберите:
   ```bash
   ./gradlew build
   ```
   На Windows:
   ```bat
   .\gradlew.bat build
   ```

4. Готовый jar будет в `build/libs/`.

> Эти jar-файлы являются сторонними и намеренно **не** включены в репозиторий. Вы
> должны предоставить их сами из своей установки Create: Deep Seas.

---

## Как это работает (технический обзор)

Исправление сочетает несколько слоёв:

- **`LivingIncomingDamageEvent` (сервер, основной):** отменяет входящий урон
  `LAVA` / `IN_FIRE` / `ON_FIRE` / `HOT_FLOOR`, когда игрок внутри герметичного
  отсека.
- **Mixin на `Entity#baseTick` (сервер):** подавляет вызов `lavaHurt()`.
- **Mixin на `LocalPlayer#baseTick` (клиент):** подавляет клиентский `lavaHurt()`,
  чтобы локально не возникали тики горения.
- **Mixin на `ScreenEffectRenderer#renderFire` (клиент):** скрывает эффект пламени
  внутри субмарины.
- **`PlayerTickEvent.Post` (сервер):** сбрасывает остаточные тики горения каждый тик.

### Определение

Чтобы понять, находится ли игрок «внутри герметичной субмарины», мод:

1. Перебирает зарегистрированные субмарины через
   `CompartmentTracker.getSubsSnapshot()`.
2. Отсекает субмарины, чей мировой AABB (`getWorldAABB`) не содержит игрока.
3. Преобразует мировую позицию игрока в локальные координаты участка (plot) через
   `subLevelAccess.logicalPose().transformPositionInverse(...)`.
4. Проверяет полученный блок по множествам `internal()` и `hull()` каждого
   герметичного `Component` из `CompartmentTracker.getCompartments(uuid)`.
5. Проверяет **позиции и ног, и глаз** (чтобы покрыть сидящих игроков, тех, кто на
   лестнице, и тех, кто в дверном проёме), а когда игрок на лестнице —
   дополнительно сканирует по вертикали, чтобы покрыть лестничные шахты выше корпуса.

Это повторяет внутреннюю логику самого CDS, но избегает публичных методов CDS,
которые работают только при вызове *изнутри* sub-level.

---

## Совместимость

- **Minecraft:** 1.21.1
- **Загрузчик:** NeoForge 21.1+
- **Требуется:** Create: Deep Seas (опциональная зависимость; без него мод ничего
  не делает)

Если вы обнаружите проблему совместимости с другим модом, пожалуйста, создайте issue.

---

## Лицензия

Распространяется под лицензией **MIT**. Вы можете свободно использовать, копировать,
изменять и распространять этот код, в том числе в других проектах или модах, при
условии сохранения указания авторства. Полный текст — в файле [`LICENSE`](LICENSE).

Любой может переиспользовать этот код — включая авторов Create: Deep Seas, если они
захотят интегрировать исправление напрямую.
