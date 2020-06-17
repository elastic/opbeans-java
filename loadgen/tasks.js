const { Chromeless } = require('chromeless');

var baseUrl = process.env.OPBEANS_BASE_URL || 'http://localhost:8080';
var url = baseUrl
var NUM_IPS = 1000;
var RANDOM_IPS = loadRandomIPs();

const chromeless = new Chromeless({ launchChrome: true,  waitTimeout: 30000 });

function sleep(ms){
    return new Promise(resolve=>{
        setTimeout(resolve,ms)
    })
}

async function run() {
    url = await chromeless
        .goto(url)
        .setExtraHTTPHeaders({
            'X-Forwarded-For': selectRandomIP()
        })
        .evaluate((baseUrl, url) => {
            var links = document.querySelectorAll('a[href^="/"]');
            var uniq_links = {};
            for ( var i=0, len=links.length; i < len; i++ ) {
                uniq_links[links[i].href] = links[i].href;
            }
            links = new Array();
            for ( var key in uniq_links ) {
                links.push(uniq_links[key]);
            }
            console.log(links)
            if (links && links.length) {
                var i = Math.floor(Math.random()*links.length);
                return links[i];
            } else {
                //no links to follow so return to the base
                return baseUrl;
            }
        }, baseUrl, url);
    console.log(url);
}

async function safe_run() {
    console.log('Starting from baseurl: '+url)
    for(;;) {
        run().catch(async (error) => {
            console.log("failed for url")
        console.log(error);
    });
        await sleep(6000 + Math.floor(Math.random()*10000));
    }
}

function selectRandomIP() {
    return RANDOM_IPS[Math.floor(Math.random() * RANDOM_IPS.length)];
}

function loadRandomIPs() {
    var IPs = [];
    while (IPs.length < NUM_IPS) {
        var randomIP = randomIp();
        if (IPs.indexOf(randomIP) === -1) {
            IPs.push(randomIP);
        }
    }
    return IPs;
}


function randomByte () {
    return Math.round(Math.random()*256);
}

function isPrivate(ip) {
    return /^10\.|^192\.168\.|^172\.16\.|^172\.17\.|^172\.18\.|^172\.19\.|^172\.20\.|^172\.21\.|^172\.22\.|^172\.23\.|^172\.24\.|^172\.25\.|^172\.26\.|^172\.27\.|^172\.28\.|^172\.29\.|^172\.30\.|^172\.31\./.test(ip);
}

function randomIp() {
    var ip = randomByte() +'.' +
        randomByte() +'.' +
        randomByte() +'.' +
        randomByte();
    if (isPrivate(ip)) { return randomIp(); }
    return ip;
}

safe_run().catch(console.error.bind(console))
