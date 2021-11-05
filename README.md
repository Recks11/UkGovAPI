# UK SponsorList API

<a href="https://github.com/Recks11/UkGovAPI/actions?query=branch%release+event%3Apush">
<img src="https://github.com/Recks11/UkGovAPI/actions/workflows/heroku-release.yml/badge.svg?branch=release" alt="Deploy Status"/> </a>


This is the API for the Uk Government Visa Sponsor list. I made this because downloading and checking a PDF or CSV file EVERY TIME is annoying. It uses no database and actively downloads the list and parses it on request. due to the io requirements, it is made using reactive spring to maximise the use of resources.

### LOAD TEST
currently deployed on heroku on a shared 512MB RAM 1 Core CPU, it can handle 50 clients per second within reasonable latency. beyond that, latency increases linearly.
