# Feature Implementation Plan: Emoji/Icon Support & Projects

## Overview

This document outlines the implementation plan for two new features:
1. **Emoji/Icon Support**: Allow users to assign emojis or custom image icons to chats, workspaces, and projects
2. **Projects**: Add a flat organizational layer for grouping sessions within workspaces

## Phase 1: Data Model Extensions

### Session Metadata
Extend `SidebarSessionItem` type in `apps/app/src/app/types.ts`:
```typescript
export type SidebarSessionItem = {
  id: string;
  title: string;
  slug?: string | null;
  parentID?: string | null;
  projectID?: string | null;  // NEW: Associate session with project
  icon?: SessionIcon | null;   // NEW: Custom icon
  time?: {
    updated?: number | null;
    created?: number | null;
  };
  directory?: string | null;
};

export type SessionIcon =
  | { type: 'emoji'; value: string }           // e.g., { type: 'emoji', value: '🚀' }
  | { type: 'image'; dataUrl: string };        // e.g., { type: 'image', dataUrl: 'data:image/png;base64,...' }
```

### Workspace Metadata
Extend `WorkspaceInfo` type in `apps/app/src/app/lib/tauri.ts`:
```typescript
export type WorkspaceInfo = {
  // ... existing fields ...
  icon?: SessionIcon | null;     // NEW: Custom workspace icon
  projects?: Project[];          // NEW: List of projects in workspace
};
```

### Project Type
Add new `Project` type in `apps/app/src/app/types.ts`:
```typescript
export type Project = {
  id: string;
  workspaceId: string;
  name: string;
  icon?: SessionIcon | null;
  color?: string | null;          // Optional accent color
  createdAt: number;
  updatedAt: number;
};
```

## Phase 2: Icon Picker Component

### Component Structure
Create `apps/app/src/app/components/common/icon-picker.tsx`:

```typescript
/**
 * IconPicker Component
 *
 * Features:
 * - Tab 1: Emoji list (searchable, categorized)
 * - Tab 2: Image upload (with preview, max 1MB, converts to data URL)
 * - Tab 3: None (remove icon)
 *
 * Props:
 * - value?: SessionIcon | null
 * - onChange: (icon: SessionIcon | null) => void
 * - onClose: () => void
 */
```

### Emoji List
- Use a curated list of common emojis (work, objects, symbols, animals, etc.)
- Group by category with tabs
- Search/filter functionality
- Grid layout with hover states

### Image Upload
- File input accepting image/*
- Preview selected image
- Validate size (max 1MB recommended)
- Convert to base64 data URL for storage
- Crop/resize to 32x32 or 64x64 for consistency

## Phase 3: Storage Layer

### Local Storage (IndexedDB)
Store icon metadata in IndexedDB alongside session/workspace data:
- Key: `session-icons-${sessionId}` or `workspace-icons-${workspaceId}` or `project-icons-${projectId}`
- Value: `SessionIcon` object

### Persistence
- Save icons immediately on selection
- Load icons when rendering sidebar
- Clear icons when session/workspace/project is deleted

## Phase 4: UI Integration

### Session List (`workspace-session-list.tsx`)
Modify session row rendering (around line 425-432):
```tsx
<div class="mr-2.5 flex min-w-0 flex-1 items-center gap-2">
  {/* NEW: Display icon before title */}
  <Show when={session().icon}>
    <SessionIconDisplay icon={session().icon!} size={16} />
  </Show>

  {/* Existing expand/collapse chevron */}
  <Show when={hasChildren()}>
    <button>...</button>
  </Show>

  {/* Existing active indicator */}
  <Show when={isSessionActive()}>
    <span class="h-1.5 w-1.5 shrink-0 rounded-full bg-amber-9" />
  </Show>

  <span class="block min-w-0 truncate">{displayTitle()}</span>
</div>
```

### Icon Display Component
Create `apps/app/src/app/components/common/session-icon-display.tsx`:
```tsx
/**
 * SessionIconDisplay Component
 *
 * Renders either emoji or image icon
 * Props:
 * - icon: SessionIcon
 * - size: number (e.g., 16, 20, 24)
 */
```

### Context Menu Integration
Add "Change icon..." option to session/workspace context menus:
- Position IconPicker as modal/popover
- Save selection via storage layer
- Update UI reactively

## Phase 5: Projects Implementation

### Project Store
Create `apps/app/src/app/context/projects-store.ts`:
```typescript
/**
 * Projects Store
 *
 * Features:
 * - CRUD operations for projects
 * - Filter sessions by project
 * - Flat organization (projects don't nest)
 * - Store in IndexedDB per workspace
 */
```

### Project Sidebar Section
Modify `workspace-session-list.tsx` to add Projects section:
- Render projects above session list
- Each project is expandable to show sessions
- Clicking project filters/shows only those sessions
- "Uncategorized" section for sessions without projectID

### Project Management UI
- Create project modal (`apps/app/src/app/components/projects/create-project-modal.tsx`)
- Edit project modal (name, icon, color)
- Delete project (keeps sessions, just clears projectID)
- Assign session to project via context menu

## Phase 6: Data Migration

### Backward Compatibility
- Icons are optional (null by default)
- Projects are optional (sessions without projectID work as before)
- Existing workspaces/sessions continue to function

### IndexedDB Schema
```typescript
// Example structure
{
  'session-metadata': {
    [sessionId]: {
      icon: SessionIcon | null,
      projectID: string | null
    }
  },
  'projects': {
    [workspaceId]: Project[]
  }
}
```

## Implementation Order

1. ✅ **Rebranding** (Completed)
2. **Data Types** (Add SessionIcon, Project types)
3. **Icon Picker Component** (Emoji + Image upload)
4. **SessionIconDisplay Component**
5. **Storage Layer** (IndexedDB read/write)
6. **Session List UI Integration** (Show icons)
7. **Context Menu Integration** (Change icon)
8. **Projects Data Model** (Project type, store)
9. **Projects UI** (Sidebar section, create/edit/delete)
10. **Session-to-Project Assignment** (Context menu, drag-drop)
11. **Testing & Polish**

## Testing Checklist

- [ ] Can assign emoji to session
- [ ] Can upload custom image to session
- [ ] Can remove icon from session
- [ ] Icons persist across app restart
- [ ] Icons display correctly in sidebar
- [ ] Can assign emoji to workspace
- [ ] Can upload custom image to workspace
- [ ] Can create project
- [ ] Can edit project name/icon
- [ ] Can delete project (sessions remain)
- [ ] Can assign session to project
- [ ] Can filter sessions by project
- [ ] Sessions without project show in "Uncategorized"
- [ ] All features work in both local and remote workspaces

## Technical Considerations

### Performance
- Lazy load emoji list (virtualized for large lists)
- Compress uploaded images before storing
- Cache rendered icons in memory
- Use CSS sprites for common emojis

### Accessibility
- Icon picker keyboard navigable
- Emoji/icon has aria-label with description
- Color contrast for project colors
- Screen reader announces icon changes

### Security
- Validate uploaded image format
- Limit image file size (prevent DoS)
- Sanitize data URLs before rendering
- No execution of arbitrary code from images

## Future Enhancements (Out of Scope)

- Nested projects (if needed later)
- Project templates
- Bulk project assignment
- Project-level settings/permissions
- Icon animation/badges
- Team-shared icon libraries
