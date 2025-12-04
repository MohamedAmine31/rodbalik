#!/bin/bash
# Setup script for automatic cleanup of expired verification codes
# This script sets up a system cron job to run the cleanup every 10 minutes

# Get the absolute path to the project directory
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Create the cron job command
CRON_COMMAND="cd $PROJECT_DIR && python manage.py runcrons >> /var/log/django_cron.log 2>&1"

# Check if the cron job already exists
CRON_EXISTS=$(crontab -l 2>/dev/null | grep -c "runcrons" || true)

if [ "$CRON_EXISTS" -gt 0 ]; then
    echo "Cron job for cleanup already exists. Skipping..."
else
    # Add the cron job to run every 10 minutes
    (crontab -l 2>/dev/null; echo "*/10 * * * * $CRON_COMMAND") | crontab -
    echo "Cron job added successfully!"
    echo "The cleanup will run every 10 minutes."
fi

echo "Current cron jobs:"
crontab -l