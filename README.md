# SOTIP-Backend
SOTIP-Backend are a group of AWS Lambda functions that implement 4 API endpoints.

## API Endpoints
* IT projects: /projects/{date}
* Sotip Charts: /charts/{date}
* IT investments: /investments/{date}
* US Agencies: /agencies/{date}

## Relationship to IT Dashboard Data Feeds
The [IT Dashboard](https://itdashboard.gov) provides data on IT spending for certain US government agencies and administrations.  
* The IT Projects API Endpoint returns the same data as the [projects data feed](https://itdashboard.gov/api/v1/ITDB2/dataFeeds/projects) provided by IT Dashboard.
* The IT Investments combines the data from 3 data feeds offered by the IT Dashboard:
... https://itdashboard.gov/api/v1/ITDB2/dataFeeds/businessCase
... https://itdashboard.gov/api/v1/ITDB2/dataFeeds/contracts
... https://itdashboard.gov/api/v1/ITDB2/dataFeeds/investmentRelatedURLs

# Lambda Functions
The code for each Lambda function is placed under a separate branch.
