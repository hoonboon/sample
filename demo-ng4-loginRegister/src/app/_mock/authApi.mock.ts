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
      
      })
      // call materialize and de-materialize to ensure delay even if an error is thrown (https://github.com/Reactive-Extensions/RxJS/issues/648) 
      .materialize()
      .delay(500)
      .dematerialize();
  }
  
}

export let authApiMockProvider = {
    // use fake backend in place of Http service for backend-less development
    provide: HTTP_INTERCEPTORS,
    useClass: AuthApiMock,
    multi: true
};
