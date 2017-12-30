import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import 'rxjs/add/operator/map';

import { LoggerService } from './logger.service';

@Injectable()
export class AuthenticateService {
  
  static readonly baseUrl = '/api/authenticate';
  
  constructor(
      private logger: LoggerService,
      private http: HttpClient) { }

  login(username: string, password: string) {
    this.logger.info('login() start');
    return this.http.post<any>(AuthenticateService.baseUrl, { username: username, password: password })
        .map(user => {
          // login successful if there's a JWT token in the response
          if (user && user.token) {
            this.logger.info('login success');
            // store user details and JWT token in local storage to keep user logged in between page refreshes
            localStorage.setItem('currentUser', JSON.stringify(user));
          } else {
            this.logger.info('login failed');
          }
          
          return user;
        });
  }
  
  logout() {
    this.logger.info('logout() start');
    // remove user from local storage to log user out
    localStorage.removeItem('currentUser');
  }
  
}