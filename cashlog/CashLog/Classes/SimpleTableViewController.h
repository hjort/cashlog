//
//  SimpleTableViewController.h
//  SimpleTable
//
//  Created by Daniel Poit on 02/02/10.
//  Copyright __MyCompanyName__ 2010. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SimpleTableViewController : UIViewController {
	
	IBOutlet UITableView *tblSimpleTable;
	IBOutlet UITextField *txtMemo;
	IBOutlet UITextField *txtValor;
	IBOutlet UITextField *txtInitBalance;
	IBOutlet UILabel *lblCurrBalance;
	IBOutlet UILabel *lblInitBalance;
	IBOutlet UIButton *cmdAdd;
	
	NSMutableArray *arryData;
	NSMutableArray *valorText;	
	
	NSString *fullValorFile;
	NSString *fullMemoFile;
	NSString *oldValorFile;
	NSString *oldMemoFile;
	NSString *initBalanceFile;
	
	float oldBalance;
	float currBalance;
	float initBalance;

	int EditingCell;
	int AddToCell;
	
}

-(IBAction) EditTable:(id)sender;
-(IBAction) mainAddButtonTouch;

@end

