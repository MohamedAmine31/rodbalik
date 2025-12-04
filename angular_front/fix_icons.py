import re

# Read the FontAwesome index file
with open(r'c:\Users\user\admin_rodbalik\node_modules\@fortawesome\free-solid-svg-icons\index.d.ts', 'r') as f:
    fa_content = f.read()

# Extract all exported icon names
fa_icons = set(re.findall(r'export const (fa\w+): IconDefinition;', fa_content))

print(f'Found {len(fa_icons)} icons in FontAwesome package')

# Read the current fontawesome-icons.ts file
with open(r'c:\Users\user\admin_rodbalik\src\app\fontawesome-icons.ts', 'r') as f:
    current_content = f.read()

# Extract icons currently being imported
current_icons = set(re.findall(r'  (fa\w+),', current_content))

# Find valid icons (intersection)
valid_icons = sorted(current_icons & fa_icons)

print(f'Found {len(valid_icons)} valid icons to import')

# Generate new import
new_import_lines = ['import {']
for icon in valid_icons:
    new_import_lines.append(f'  {icon},')
new_import_lines.append('} from \'@fortawesome/free-solid-svg-icons\';')
new_import = '\n'.join(new_import_lines)

# Replace the import block
# Find import block boundaries
lines = current_content.split('\n')
import_start = None
import_end = None
for i, line in enumerate(lines):
    if line.strip() == 'import {':
        import_start = i
    if line.strip() == '} from \'@fortawesome/free-solid-svg-icons\';':
        import_end = i
        break

# Get content before and after
before = '\n'.join(lines[:import_start])
after = '\n'.join(lines[import_end+1:])

# Combine
new_content = before + '\n' + new_import + '\n' + after

# Write back
with open(r'c:\Users\user\admin_rodbalik\src\app\fontawesome-icons.ts', 'w') as f:
    f.write(new_content)

print('File updated with only valid FontAwesome icons')