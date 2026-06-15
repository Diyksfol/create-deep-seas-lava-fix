# Mod page descriptions (CurseForge / Modrinth)

Both CurseForge and Modrinth accept Markdown. Paste the relevant block into the
description field. English first, Russian below it.

---
---

## ENGLISH

# Create: Deep Seas - Lava Fix

**Stop burning inside your own submarine.**

A lightweight fix for [Create: Deep Seas](https://www.curseforge.com/minecraft/mc-mods/create-deep-seas)
(CDS). In CDS, a sealed submarine pumps out all fluid — including lava — so you can
walk around inside as if in open air. But the game kept hurting players with
fire/lava damage inside that emptied space. This mod fixes that.

### ✅ What it fixes
- 🔥 Fire/lava damage while inside a sealed, lava-submerged submarine
- 🚪 Damage in hatches and doorways at the lava surface (entering / exiting)
- 🪑 Damage while in Create seats, on ladders, or in doorways inside the sub
- ♾️ Endless burning after the hull is breached and re-sealed
- 👁️ The fire overlay (flames at the bottom of the screen) while inside

### ❌ What it can't fix (these are internal to Create: Deep Seas)
- The **lava surface texture** is still visible inside the sub (CDS hides water but not lava).
- **Trapdoors don't work as climbable ladders** when below the lava/water level inside the sub. (Same happens with water — vanilla's climb needs a fluid present, which CDS hides.)
- **Fire placed by the player inside the sub** still produces a fluid-like effect.
- **Lava/water poured directly inside** deals no damage — this is an intentional CDS feature, not a bug.

These would require changing core Create: Deep Seas behaviour, so they're left alone
to avoid breaking the sealed-sub mechanic. If the CDS authors want to address them,
this mod's source is open for them to reuse.

### 📦 Requirements
- Minecraft **1.21.1**
- **NeoForge** 21.1+
- **Create: Deep Seas** (the mod safely does nothing if CDS isn't installed)

### 📖 Source & license
- Full source code: **https://github.com/Diyksfol/create-deep-seas-lava-fix**
- License: **MIT** — anyone may reuse this code, including the Create: Deep Seas authors.

### ℹ️ A note on how this was made
This mod was developed with the help of an AI assistant, with me acting primarily as
the tester and director throughout. Sharing this openly in the spirit of transparency.

---
---

## РУССКИЙ

# Create: Deep Seas - Lava Fix

**Перестаньте гореть внутри собственной субмарины.**

Лёгкое исправление для [Create: Deep Seas](https://www.curseforge.com/minecraft/mc-mods/create-deep-seas)
(CDS). В CDS герметичная субмарина откачивает всю жидкость — включая лаву — так что
внутри можно ходить как по воздуху. Но игра продолжала наносить игроку урон от
огня/лавы в этом очищенном пространстве. Этот мод исправляет проблему.

### ✅ Что исправляет
- 🔥 Урон от огня/лавы внутри герметичной субмарины, погружённой в лаву
- 🚪 Урон в люках и дверных проёмах на уровне поверхности лавы (вход/выход)
- 🪑 Урон в сиденьях из Create, на лестницах и в дверных проёмах внутри субмарины
- ♾️ Бесконечное горение после нарушения и восстановления герметичности
- 👁️ Эффект пламени (огонь внизу экрана) внутри субмарины

### ❌ Что исправить нельзя (это внутренняя логика Create: Deep Seas)
- **Текстура поверхности лавы** всё ещё видна внутри субмарины (CDS скрывает воду, но не лаву).
- **Люки не работают как лестницы**, если находятся ниже уровня лавы/воды внутри субмарины. (То же с водой — ванильное карабканье требует наличия жидкости, которую CDS скрывает.)
- **Огонь, поставленный игроком внутри субмарины**, всё ещё создаёт эффект жидкости.
- **Лава/вода, налитая прямо внутрь**, не наносит урона — это намеренная особенность CDS, а не баг.

Их исправление потребовало бы изменения базового поведения Create: Deep Seas,
поэтому они оставлены как есть, чтобы не сломать механику герметичной субмарины.
Если авторы CDS захотят их устранить, исходный код этого мода открыт для них.

### 📦 Требования
- Minecraft **1.21.1**
- **NeoForge** 21.1+
- **Create: Deep Seas** (без него мод просто ничего не делает)

### 📖 Исходный код и лицензия
- Полный исходный код: **https://github.com/Diyksfol/create-deep-seas-lava-fix**
- Лицензия: **MIT** — любой может переиспользовать этот код, включая авторов Create: Deep Seas.

### ℹ️ Как был сделан мод
Этот мод разработан с помощью ИИ-ассистента; я выступал в основном в роли
тестировщика и постановщика задач. Делюсь этим открыто в духе прозрачности.
