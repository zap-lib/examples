import os from 'os';
import QRCode from 'qrcode';

function ip() {
  const interfaces = os.networkInterfaces();

  for (let i in interfaces) {
    const found = interfaces[i];
    if (found) {
      for (let address of found) {
        if (address.family === 'IPv4' && !address.internal) {
          return address.address;
        }
      }
    }
  }

  return '0.0.0.0';
}

(() => {
  QRCode.toCanvas(document.querySelector('#qr'), ip(), {});
})();
