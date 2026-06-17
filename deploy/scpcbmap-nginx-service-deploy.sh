echo "Creating systemd service"
cp scpcbmap.service /etc/systemd/system/scpcbmap.service
if [ $? -ne 0 ]
then
    echo "Command 'cp scpcbmap.service /etc/systemd/system/scpcbmap.service' failed" >> outputfile
    exit 1
fi
echo "Starting systemd service"
systemctl daemon-reload
systemctl restart scpcbmap
echo "Restarting nginx"
systemctl restart nginx
echo "Ready to work"