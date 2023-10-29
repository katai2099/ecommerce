#!/bin/sh
ssh -i "$EC2keyfile" -o StrictHostKeyChecking=no $EC2_USER@16.170.227.116 <<EOF
echo "Deploying to EC2 instance"
set -euo pipefail
docker rmi -f katai2099/ecommerce-backend || true
docker rm -f ecommerce-backend || true
docker run --name ecommerce-backend -d -p 5000:8081 -v $HOME/.aws/:/root/.aws:ro --restart=always -e MAIL_SERVER_USERNAME=$MAIL_SERVER_USER -e MAIL_SERVER_PASSWORD=$MAIL_SERVER_PASS -e JWT_SIGNING_KEY=$JWT_SECRET -e STRIPE_API_KEY=$STRIP_API_KEY -e RDS_USERNAME=$RDS_USER -e RDS_PASSWORD=$RDS_PASS katai2099/ecommerce-backend
EOF