import re

# Read the file
with open(r'c:\Users\user\admin_rodbalik\src\app\fontawesome-icons.ts', 'r') as f:
    content = f.read()

# Find all fa* icons in the import block
icons = re.findall(r'  (fa\w+),', content)

# Get unique icons and sort them
unique_icons = sorted(set(icons))

# Print the import statement
print('import {')
for icon in unique_icons:
    print(f'  {icon},')
print('} from \'@fortawesome/free-solid-svg-icons\';')