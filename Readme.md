#Steps to upload image

1) aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 911075010171.dkr.ecr.us-east-1.amazonaws.com
2) docker tag search-service:latest 911075010171.dkr.ecr.us-east-1.amazonaws.com/search-service:latest
3) docker push 911075010171.dkr.ecr.us-east-1.amazonaws.com/search-service:latest
