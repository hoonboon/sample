import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse, HTTP_INTERCEPTORS } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/delay';
import 'rxjs/add/operator/mergeMap';
import 'rxjs/add/operator/materialize';
import 'rxjs/add/operator/dematerialize';

import { LoggerService } from '../common/services/logger.service';
import { UserService } from '../common/services/user.service';
import { AuthenticateService } from '../common/services/authenticate.service';

@Injectable()
export class AuthApiMock implements HttpInterceptor {
  
  constructor(
      private logger: LoggerService) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.logger.info('intercept() start');
    
    // array in local storage for all registered users
    let users: any[] = JSON.parse(localStorage.getItem('users')) || [];
    
    // wrap in delayed observable to simulate server api call
    return Observable.of(null).mergeMap(() => {
      
        // create user
        if (request.url.endsWith(UserService.baseUrl) && request.method === 'POST') {
          this.logger.info('create user start');
          
          // get new user object from request body
          let newUser = request.body;
          
          // validation
          let duplicateUser = users.filter(user => { return user.username === newUser.username }).length;
          if (duplicateUser) {
            return Observable.throw('Username "' + newUser.username + '" is already taken.');
          }
          
          // save new user
          newUser.id = users.length + 1;
          users.push(newUser);
          localStorage.setItem('users', JSON.stringify(users));
          
          this.logger.info('create user end');
          
          // respond 200 OK
          return Observable.of(new HttpResponse({ status: 200 }));
        }
        
        // authenticate
        else if (request.url.endsWith(AuthenticateService.baseUrl) && request.method === 'POST') {
          this.logger.info('authenticate start');
          
          // find if any user matches login credentials
          let filteredUsers = users.filter(user => {
            return user.username === request.body.username && user.password === request.body.password;
          });
          
          // if login details are valid return 200 OK with user details + fake JWT token
          if (filteredUsers.length) {
            // return only the 1st match
            let user = filteredUsers[0];
            
            let body = {
                id : user.id,
                username : user.username,
                firstName : user.firstName,
                lastName : user.lastName,
                token : 'dummy-jwt-token'
            };
            
            this.logger.info('authenticate success');
            
            return Observable.of(new HttpResponse({ status: 200, body: body }));
          }
          // else return 400 Bad Request
          else {
            this.logger.info('authenticate error');
            return Observable.throw('Username or password is incorrect');
          }
          
        }
        
        // get users
        else if (request.url.endsWith(UserService.baseUrl) && request.method === 'GET') {
          this.logger.info('get users start');
          
          // check dummy JWT token in header (actual checking should be done at Server side)
          // if valid return users
          if (this.checkDummyJwtToken(request)) {
            return Observable.of(new HttpResponse({ status: 200, body: users }));
          } 
          // else return 401 Not Authorized
          else {
            return Observable.throw('Unauthorized Access');
          }
          
        }
        
        // delete user by id
        else if (request.url.match(/\/api\/users\/\d+$/) && request.method === 'DELETE') {
          this.logger.info('delete start');
          
          // check dummy JWT token in header (actual checking should be done at Server side)
          if (this.checkDummyJwtToken(request)) {
            // find user by id in the array and delete it
            let urlParts = request.url.split('/');
            let id = parseInt(urlParts[urlParts.length - 1]);
            let recordFound = false;
            
            for (let i = 0; i < users.length; i++) {
              let user = users[i];
              if (user.id === id) {
                recordFound = true;
                users.splice(i, 1);
                localStorage.setItem('users', JSON.stringify(users));
                break;
              }
            }
            
            if (recordFound) {
              // respond 200 OK
              return Observable.of(new HttpResponse({ status: 200 }));
            } else {
              return Observable.throw('Record not found');
            }
          } 
          // else return 401 Not Authorized
          else {
            return Observable.throw('Unauthorized Access');
          }
        }
        
        // if non-matched, throw error
        else {
          this.logger.error('Unimplemented API: ' + JSON.stringify(request));
          //return Observable.throw('Unimplemented API: ' + request.url + ', ' + request.method);
        }
        
        // continue with next interceptor(s)
        return next.handle(request);

      })
      // call materialize and de-materialize to ensure delay even if an error is thrown (https://github.com/Reactive-Extensions/RxJS/issues/648) 
      .materialize()
      .delay(500)
      .dematerialize();
  }
  
  private checkDummyJwtToken(request: HttpRequest<any>) {
    return request.headers.get('Authorization') === 'Bearer dummy-jwt-token';
  }
  
}

export let authApiMockProvider = {
    // use fake backend in place of Http service for backend-less development
    provide: HTTP_INTERCEPTORS,
    useClass: AuthApiMock,
    multi: true
};
