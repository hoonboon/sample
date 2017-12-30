import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';

import { LoggerService } from '../services/logger.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  
  constructor(
      private logger: LoggerService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.logger.info('intercept() start');
    
    // add authorization header with JWT token if available
    let currentUser = JSON.parse(localStorage.getItem('currentUser'));
    
    if (currentUser && currentUser.token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${currentUser.token}`
        }
      });
    }
    else {
      this.logger.info('JWT token not available');
    }
    
    return next.handle(request);
  }
  
}
