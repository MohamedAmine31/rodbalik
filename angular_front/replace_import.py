with open(r'c:\Users\user\admin_rodbalik\src\app\fontawesome-icons.ts', 'r') as f:
    content = f.read()

# Split the content
lines = content.split('\n')

# Find the import block
import_start = None
import_end = None
for i, line in enumerate(lines):
    if line.strip() == 'import {':
        import_start = i
    if line.strip() == '} from \'@fortawesome/free-solid-svg-icons\';':
        import_end = i
        break

# Get content before and after import
before_import = '\n'.join(lines[:import_start])
after_import = '\n'.join(lines[import_end+1:])

# Read the new import content
with open(r'c:\Users\user\admin_rodbalik\extract_icons.py', 'r') as f:
    script_content = f.read()

# Execute the script to get the new import
import re
icons = re.findall(r'  (fa\w+),', content)
unique_icons = sorted(set(icons))
new_import_lines = ['import {']
for icon in unique_icons:
    new_import_lines.append(f'  {icon},')
new_import_lines.append('} from \'@fortawesome/free-solid-svg-icons\';')
new_import = '\n'.join(new_import_lines)

# Combine everything
new_content = before_import + '\n' + new_import + '\n' + after_import

# Write back
with open(r'c:\Users\user\admin_rodbalik\src\app\fontawesome-icons.ts', 'w') as f:
    f.write(new_content)

print('File updated successfully')